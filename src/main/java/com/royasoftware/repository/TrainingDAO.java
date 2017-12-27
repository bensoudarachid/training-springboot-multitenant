package com.royasoftware.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.royasoftware.model.Training;

/**
 * The AccountRepository interface is a Spring Data JPA data repository for
 * Account entities. The AccountRepository provides all the data access
 * behaviors exposed by <code>JpaRepository</code> and additional custom
 * behaviors may be defined in this interface.
 */
@Repository
public class TrainingDAO {
	@PersistenceContext
	private EntityManager em;

	/**
	 * Query for a single Account entity by username.
	 *
	 * @param username
	 *            A String username value to query the repository.
	 * @return An Account or <code>null</code> if none found.
	 */

//	Query("SELECT tr FROM Training tr WHERE tr.id = :trainingid")
	public Training findById(Long id) { //@Param("trainingid") 
		TypedQuery<Training> query = em.createQuery("SELECT tr FROM Training tr WHERE tr.id = :trainingid", Training.class);
		query.setParameter("trainingid", id);
		return query.getSingleResult();
	}

}