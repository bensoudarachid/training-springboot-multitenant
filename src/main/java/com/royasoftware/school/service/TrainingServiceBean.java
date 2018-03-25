package com.royasoftware.school.service;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.google.common.util.concurrent.Uninterruptibles;
import com.royasoftware.school.cluster.SpringExtension;
import com.royasoftware.school.model.Training;
//import com.royasoftware.school.repository.TrainingRepositoryImpl;
//import com.royasoftware.school.repository.TrainingRepository;
import com.royasoftware.school.repository.TrainingRepository;

import akka.actor.ActorSystem;
/**
 * Manage the data from database from Role table user
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
//@Scope("prototype")
public class TrainingServiceBean implements TrainingService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * The Spring Data repository for Account entities.
	 */
//	@Autowired(required = false)
//	private TrainingRepository trainingRepository;
	@Autowired(required = false)
	private TrainingRepository trainingRepository;
//	@Autowired(required = false)
//	private MongoTrainingRepository mongoTrainingRepository;	
	@Autowired
	private SpringExtension springExtension;

	@Autowired(required = false)
	private ActorSystem system;
	/**
	 * Get by id
	 * 
	 * @param id
	 * @return
	 */
	@Override
	@Cacheable(value = "training", key = "#id")
	public Training findById(Long id) {
		logger.info("getTraining "+id);
		Uninterruptibles.sleepUninterruptibly(3000, TimeUnit.MILLISECONDS);
		Training training = trainingRepository.findById(id);
		return training;
	}

//	@Override
//	public Training findById(String id) {
//		Training training = trainingRepository.findOne(id);
//		return training;
//	}

	/**
	 * File Role by code
	 * 
	 * @param code
	 *            - the code of the role
	 * @return Role object
	 */

	@Override
	@Cacheable(value = "trainings")
	public Collection<Training> findAll() {
		logger.info("service findAll trainings");
		Uninterruptibles.sleepUninterruptibly(3000, TimeUnit.MILLISECONDS);
		
		// use the Spring Extension to create props for a named actor bean
//		ActorRef counter = system.actorOf(springExtension.props("MyCountingActor")); //, "counter"

		// tell it to count three times
//		counter.tell(new Count(), null);
//		counter.tell(new Count(), null);
//		counter.tell(new Count(), null);

		// print the result
//		FiniteDuration duration = FiniteDuration.create(3, TimeUnit.SECONDS);
//		Future<Object> result = ask(counter, new Get(), Timeout.durationToTimeout(duration));
//		try {
//			System.out.println("Got back " + Await.result(result, duration));
//		} catch (Exception e) {
//			System.err.println("Failed getting result: " + e.getMessage());
//		} 
//		finally {
//			system.terminate();
//
//		}
//		return mongoTrainingRepository.findAll();
		return trainingRepository.findAll();
	}

	@Override
	public Training saveTraining(Training training) {
		return trainingRepository.save(training);
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@CachePut(value = "training", key = "#training.id")
	@CacheEvict(value = "trainings", allEntries=true)
	public Training updateTraining(Training training){
		logger.info("save training insha2allah = "+training.getId());
		return trainingRepository.save(training);
	}

	@CacheEvict(value = "training", allEntries=true)
	@DeleteMapping("/{id}")
	public void deleteTraining(Training trainingParam){
		trainingRepository.delete(trainingParam);
	}

}
