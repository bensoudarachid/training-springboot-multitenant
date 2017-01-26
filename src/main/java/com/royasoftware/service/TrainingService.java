package com.royasoftware.service;

import com.royasoftware.model.Account;
import com.royasoftware.model.Training;

import java.util.Collection;

/**
 * Created by christospapidas on 24012016--.
 */
public interface TrainingService {

    Training findById(Long trainingId);
    
    Training saveTraining(Training training);

	Training updateTraining(Training training);

	void deleteTraining(Training trainingParam);

	public Collection<Training> findAll();
}
