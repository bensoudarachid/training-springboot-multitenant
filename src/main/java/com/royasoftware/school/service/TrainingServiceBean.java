package com.royasoftware.school.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
		Training training = trainingRepository.findById(id);
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
//		logger.info("find all trainings");
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
