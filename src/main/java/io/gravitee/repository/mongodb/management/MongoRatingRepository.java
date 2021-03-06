/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.repository.mongodb.management;

import io.gravitee.common.data.domain.Page;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.RatingRepository;
import io.gravitee.repository.management.api.search.Pageable;
import io.gravitee.repository.management.model.Rating;
import io.gravitee.repository.mongodb.management.internal.api.RatingMongoRepository;
import io.gravitee.repository.mongodb.management.internal.model.RatingMongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

/**
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author GraviteeSource Team
 */
@Component
public class MongoRatingRepository implements RatingRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoRatingRepository.class);

    @Autowired
    private RatingMongoRepository internalRatingRepository;

    @Override
    public Optional<Rating> findById(String id) throws TechnicalException {
        LOGGER.debug("Find rating by ID [{}]", id);
        final RatingMongo rating = internalRatingRepository.findOne(id);
        LOGGER.debug("Find rating by ID [{}] - Done", id);
        return ofNullable(map(rating));
    }

    @Override
    public Optional<Rating> findByApiAndUser(final String api, final String user) throws TechnicalException {
        LOGGER.debug("Find rating by api [{}] and user [{}]", api, user);
        final RatingMongo rating = internalRatingRepository.findByApiAndUser(api, user);
        LOGGER.debug("Find rating by api [{}] and user [{}] - DONE", api, user);
        return ofNullable(map(rating));
    }

    @Override
    public Rating create(final Rating rating) throws TechnicalException {
        LOGGER.debug("Create rating for api [{}] by user [{}]", rating.getApi(), rating.getUser());
        final Rating createdRating = map(internalRatingRepository.insert(map(rating)));
        LOGGER.debug("Create rating for api [{}] by user [{}] - DONE", rating.getApi(), rating.getUser());
        return createdRating;
    }

    @Override
    public Page<Rating> findByApiPageable(final String api, final Pageable pageable) throws TechnicalException {
        LOGGER.debug("Find rating by api [{}] with pagination", api);
        final org.springframework.data.domain.Page<RatingMongo> ratingPageMongo =
                internalRatingRepository.findByApi(api, new PageRequest(pageable.pageNumber() - 1, pageable.pageSize(), Sort.Direction.DESC, "createdAt"));
        final List<Rating> ratings = ratingPageMongo.getContent().stream().map(this::map).collect(toList());
        final Page<Rating> ratingPage = new Page<>(ratings, ratingPageMongo.getNumber() + 1, ratingPageMongo.getNumberOfElements(), ratingPageMongo.getTotalElements());
        LOGGER.debug("Find rating by api [{}] with pagination - DONE", api);
        return ratingPage;
    }

    @Override
    public List<Rating> findByApi(final String api) throws TechnicalException {
        LOGGER.debug("Find rating by api [{}]", api);
        final List<RatingMongo> ratings = internalRatingRepository.findByApi(api);
        LOGGER.debug("Find rating by api [{}] - DONE", api);
        return ratings.stream().map(this::map).collect(toList());
    }

    @Override
    public Rating update(final Rating rating) throws TechnicalException {
        if (rating == null || rating.getId() == null) {
            throw new IllegalStateException("Rating to update must specify an id");
        }
        final RatingMongo ratingMongo = internalRatingRepository.findOne(rating.getId());
        if (ratingMongo == null) {
            throw new IllegalStateException(String.format("No rating found with id [%s]", rating.getId()));
        }
        try {
            ratingMongo.setApi(rating.getApi());
            ratingMongo.setUser(rating.getUser());
            ratingMongo.setRate(rating.getRate());
            ratingMongo.setTitle(rating.getTitle());
            ratingMongo.setComment(rating.getComment());
            ratingMongo.setCreatedAt(rating.getCreatedAt());
            ratingMongo.setUpdatedAt(rating.getUpdatedAt());
            return map(internalRatingRepository.save(ratingMongo));
        } catch (Exception e) {
            LOGGER.error("An error occurred while updating rating", e);
            throw new TechnicalException("An error occurred while updating rating");
        }
    }

    @Override
    public void delete(final String id) throws TechnicalException {
        try {
            internalRatingRepository.delete(id);
        } catch (Exception e) {
            LOGGER.error("An error occurred while deleting rating [{}]", id, e);
            throw new TechnicalException("An error occurred while deleting rating");
        }
    }

    private Rating map(final RatingMongo ratingMongo) {
        if (ratingMongo == null) {
            return null;
        }
        final Rating rating = new Rating();
        rating.setId(ratingMongo.getId());
        rating.setApi(ratingMongo.getApi());
        rating.setUser(ratingMongo.getUser());
        rating.setRate(ratingMongo.getRate());
        rating.setTitle(ratingMongo.getTitle());
        rating.setComment(ratingMongo.getComment());
        rating.setCreatedAt(ratingMongo.getCreatedAt());
        rating.setUpdatedAt(ratingMongo.getUpdatedAt());
        return rating;
    }

    private RatingMongo map(final Rating rating) {
        final RatingMongo ratingMongo = new RatingMongo();
        ratingMongo.setId(rating.getId());
        ratingMongo.setApi(rating.getApi());
        ratingMongo.setUser(rating.getUser());
        ratingMongo.setRate(rating.getRate());
        ratingMongo.setTitle(rating.getTitle());
        ratingMongo.setComment(rating.getComment());
        ratingMongo.setCreatedAt(rating.getCreatedAt());
        ratingMongo.setUpdatedAt(rating.getUpdatedAt());
        return ratingMongo;
    }
}
