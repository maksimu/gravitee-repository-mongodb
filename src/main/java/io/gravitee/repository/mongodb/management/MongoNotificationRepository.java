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

import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.NotificationRepository;
import io.gravitee.repository.management.model.Notification;
import io.gravitee.repository.mongodb.management.internal.model.NotificationMongo;
import io.gravitee.repository.mongodb.management.internal.notification.NotificationMongoRepository;
import io.gravitee.repository.mongodb.management.mapper.GraviteeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author GraviteeSource Team
 */
@Component
public class MongoNotificationRepository implements NotificationRepository {

    private final Logger LOGGER = LoggerFactory.getLogger(MongoNotificationRepository.class);

    @Autowired
    private NotificationMongoRepository internalRepo;

    @Autowired
    private GraviteeMapper mapper;

    @Override
    public List<Notification> findByUsername(String username) throws TechnicalException {
        LOGGER.debug("Find notifications by username: {}", username);
        return internalRepo.findByUsername(username).
                stream().
                map(n -> mapper.map(n, Notification.class)).
                collect(Collectors.toList());
    }

    @Override
    public Notification create(Notification item) throws TechnicalException {
        LOGGER.debug("Create notification : {}", item);
        return mapper.map(internalRepo.insert(mapper.map(item, NotificationMongo.class)), Notification.class);
    }

    @Override
    public void delete(String id) throws TechnicalException {
        LOGGER.debug("Delete notification : {}", id);
        internalRepo.delete(id);
    }
}
