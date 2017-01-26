package com.royasoftware.service;

import com.royasoftware.model.Role;
import com.royasoftware.model.Todo;
import com.royasoftware.model.Training;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.royasoftware.repository.TrainingRepository;

/**
 * Manage the data from database from Role table user
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class TrainingServiceBean implements TrainingService {

	/**
	 * The Spring Data repository for Account entities.
	 */
	@Autowired
	private TrainingRepository trainingRepository;

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
		return trainingRepository.findAll();
	}

	@Override
	public Training saveTraining(Training training) {
		return trainingRepository.save(training);
	}

	public Training updateTraining(Training training){
		return trainingRepository.save(training);
	}

	public void deleteTraining(Training trainingParam){
		trainingRepository.delete(trainingParam);
	}

}
