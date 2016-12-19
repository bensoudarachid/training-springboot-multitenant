package com.royasoftware.service;

import com.royasoftware.model.Role;
import com.royasoftware.model.Todo;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.royasoftware.repository.TodoRepository;

/**
 * Manage the data from database from Role table user
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class TodoServiceBean implements TodoService {

	/**
	 * The Spring Data repository for Account entities.
	 */
	@Autowired
	private TodoRepository todoRepository;

	/**
	 * Get by id
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Todo findById(Long id) {
		Todo todo = todoRepository.findByTodoId(id);
		return todo;
	}

	/**
	 * File Role by code
	 * 
	 * @param code
	 *            - the code of the role
	 * @return Role object
	 */
	@Override
	public Collection<Todo> findByUserId(Long userId) {
		return todoRepository.findByUserid(userId);
	}

	@Override
	public Todo saveTodo(Todo todo, Long userId) {
		todo.setUserId(userId);
		return todoRepository.save(todo);
	}
	
	public Todo updateTodo(Todo todo, Long userId){
		todo.setUserId(userId);
		return todoRepository.save(todo);
	}
	public void deleteTodo(Todo todoParam, Long userId){
		todoRepository.delete(todoParam);
	}
	
}
