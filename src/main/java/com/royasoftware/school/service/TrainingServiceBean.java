package com.royasoftware.school.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.royasoftware.school.cluster.SpringExtension;
import com.royasoftware.school.model.Training;
import com.royasoftware.school.repository.TrainingDAO;
import com.royasoftware.school.repository.TrainingRepository;

import akka.actor.ActorSystem;
/**
 * Manage the data from database from Role table user
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
//@Scope("prototype")
public class TrainingServiceBean implements TrainingService {

	/**
	 * The Spring Data repository for Account entities.
	 */
	@Autowired(required = false)
	private TrainingRepository trainingRepository;
	@Autowired(required = false)
	private TrainingDAO trainingDao;
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
	public Training findById(Long id) {
		Training training = trainingDao.findById(id);
		return training;
	}

	/**
	 * File Role by code
	 * 
	 * @param code
	 *            - the code of the role
	 * @return Role object
	 */

	@Override
	public Collection<Training> findAll() {
		
		
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
		
		return trainingRepository.findAll();
	}

	@Override
	public Training saveTraining(Training training) {
		return trainingRepository.save(training);
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public Training updateTraining(Training training){
		return trainingRepository.save(training);
	}

	public void deleteTraining(Training trainingParam){
		trainingRepository.delete(trainingParam);
	}

}
