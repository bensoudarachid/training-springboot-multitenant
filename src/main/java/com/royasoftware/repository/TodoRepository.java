package com.royasoftware.repository;

import com.royasoftware.model.Account;
import com.royasoftware.model.Todo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * The AccountRepository interface is a Spring Data JPA data repository for
 * Account entities. The AccountRepository provides all the data access
 * behaviors exposed by <code>JpaRepository</code> and additional custom
 * behaviors may be defined in this interface.
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

	/**
	 * Query for a single Account entity by username.
	 *
	 * @param username
	 *            A String username value to query the repository.
	 * @return An Account or <code>null</code> if none found.
	 */
	@Query("SELECT td FROM Todo td WHERE td.userId = :userid")
	Collection<Todo> findByUserid(@Param("userid") Long userid);

	@Query("SELECT td FROM Todo td WHERE td.id = :todoid")
	Todo findByTodoId(@Param("todoid") Long todoid);


}