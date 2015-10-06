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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import io.gravitee.repository.mongodb.management.internal.application.ApplicationMongoRepository;
import io.gravitee.repository.mongodb.management.internal.user.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.gravitee.repository.mongodb.management.internal.api.ApiMongoRepository;
import io.gravitee.repository.mongodb.management.internal.model.ApplicationMongo;
import io.gravitee.repository.mongodb.management.internal.model.UserMongo;
import io.gravitee.repository.mongodb.management.internal.team.TeamMongoRepository;
import io.gravitee.repository.mongodb.management.mapper.GraviteeMapper;
import io.gravitee.repository.api.management.ApplicationRepository;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.model.management.Application;
import io.gravitee.repository.model.management.OwnerType;

@Component
public class ApplicationRepositoryImpl implements ApplicationRepository{

	@Autowired
	private ApplicationMongoRepository internalApplicationRepo;

	@Autowired
	private ApiMongoRepository internalApiRepo;
	
	@Autowired
	private TeamMongoRepository internalTeamRepo;
	
	@Autowired
	private UserMongoRepository internalUserRepo;
	
	@Autowired
	private GraviteeMapper mapper;

	@Override
	public Set<Application> findAll() throws TechnicalException {
		
		List<ApplicationMongo> applications = internalApplicationRepo.findAll();
		return mapApplications(applications);
	}


	@Override
	public Application create(Application application) throws TechnicalException {
		ApplicationMongo applicationMongo = mapApplication(application);
		ApplicationMongo applicationMongoCreated = internalApplicationRepo.insert(applicationMongo);
		return mapApplication(applicationMongoCreated);
	}

	@Override
	public Application update(Application application) throws TechnicalException {
		
    	ApplicationMongo applicationMongo = internalApplicationRepo.findOne(application.getName());
		
		//Update, but don't change invariant other creation information 
		applicationMongo.setDescription(application.getDescription());
		applicationMongo.setUpdatedAt(application.getUpdatedAt());
		applicationMongo.setType(application.getType());
		
		if (OwnerType.USER.equals(application.getOwnerType())) {
			applicationMongo.setOwner(internalUserRepo.findOne(application.getOwner()));
		} else {
			applicationMongo.setOwner(internalTeamRepo.findOne(application.getOwner()));
		}
		
		ApplicationMongo applicationMongoUpdated = internalApplicationRepo.save(applicationMongo);
		return mapApplication(applicationMongoUpdated);
	}

	@Override
	public Optional<Application> findByName(String applicationName) throws TechnicalException {
		ApplicationMongo application = internalApplicationRepo.findOne(applicationName);
		return Optional.ofNullable(mapApplication(application));
	}

	@Override
	public void delete(String apiName) throws TechnicalException {
		internalApplicationRepo.delete(apiName);
	}
	

	@Override
	public Set<Application> findByUser(String username) throws TechnicalException {
		
		List<ApplicationMongo> applications = internalApplicationRepo.findByUser(username);
		return mapApplications(applications);
	}


	@Override
	public Set<Application> findByTeam(String teamName) throws TechnicalException {
	
		List<ApplicationMongo> applications = internalApplicationRepo.findByTeam(teamName);
		return mapApplications(applications);
	}
	
	@Override
	public int countByUser(String username) throws TechnicalException {
		try{
			return (int) internalApplicationRepo.countByUser(username);
		}catch(Exception e){
			throw new TechnicalException("Coun't by user failed", e);
		}
	}

	@Override
	public int countByTeam(String teamName) throws TechnicalException {
		return (int) internalApplicationRepo.countByTeam(teamName);	
	}
	
	private Set<Application> mapApplications(Collection<ApplicationMongo> applications){
		
		Set<Application> res = new HashSet<>();
		for (ApplicationMongo application : applications) {
			res.add(mapApplication(application));
		}
		return res;
	}
	
	private Application mapApplication(ApplicationMongo  applicationMongo) {

		if(applicationMongo == null){
			return null;
		}
		
		Application application = mapper.map(applicationMongo, Application.class);

		if(applicationMongo.getOwner() != null){
			application.setOwner(applicationMongo.getOwner().getName());
			if(applicationMongo.getOwner() instanceof UserMongo){
				application.setOwnerType(OwnerType.USER);
			}else{
				application.setOwnerType(OwnerType.TEAM);
			}
		}
		if(applicationMongo.getCreator()!= null){
			application.setCreator(applicationMongo.getCreator().getName());
		}
		
		return application;
	}
	
	private ApplicationMongo mapApplication(Application application) {

		if(application == null){
			return null;
		}
		
		ApplicationMongo applicationMongo = mapper.map(application, ApplicationMongo.class);

		if (OwnerType.USER.equals(application.getOwnerType())) {
			applicationMongo.setOwner(internalUserRepo.findOne(application.getOwner()));
		} else {
			applicationMongo.setOwner(internalTeamRepo.findOne(application.getOwner()));
		}
		applicationMongo.setCreator(internalUserRepo.findOne(application.getCreator()));
		
		return applicationMongo;
	}

	
}