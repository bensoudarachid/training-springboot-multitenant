package com.royasoftware.school.service;

import java.util.Collection;

import com.royasoftware.school.model.Account;
import com.royasoftware.school.model.Training;

/**
 * Created by christospapidas on 24012016--.
 */
public interface TrainingService {

    Training findById(Long trainingId);
//    Training findById(String trainingId);
    
    Training saveTraining(Training training);

	Training updateTraining(Training training);

	void deleteTraining(Training trainingParam);

	public Collection<Training> findAll();
}
