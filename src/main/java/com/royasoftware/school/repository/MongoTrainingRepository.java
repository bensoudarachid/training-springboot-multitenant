package com.royasoftware.school.repository;


//import org.springframework.data.mongodb.repository.MongoRepository;

import com.royasoftware.school.model.Training;

public interface MongoTrainingRepository {//extends MongoRepository<Training, String> {

    public Training findByTitle(String title);

}