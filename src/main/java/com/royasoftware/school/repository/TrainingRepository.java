package com.royasoftware.school.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.royasoftware.school.model.Account;
import com.royasoftware.school.model.Todo;
import com.royasoftware.school.model.Training;

/**
 * The AccountRepository interface is a Spring Data JPA data repository for
 * Account entities. The AccountRepository provides all the data access
 * behaviors exposed by <code>JpaRepository</code> and additional custom
 * behaviors may be defined in this interface.
 */
@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

	/**
	 * Query for a single Account entity by username.
	 *
	 * @param username
	 *            A String username value to query the repository.
	 * @return An Account or <code>null</code> if none found.
	 */

	@Query("SELECT tr FROM Training tr WHERE tr.id = :trainingid")
	Training findById(@Param("trainingid") Long id);


}