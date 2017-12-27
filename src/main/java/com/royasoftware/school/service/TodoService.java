package com.royasoftware.school.service;

import java.util.Collection;

import com.royasoftware.school.model.Account;
import com.royasoftware.school.model.Todo;

/**
 * Created by christospapidas on 24012016--.
 */
public interface TodoService {

    Todo findById(Long todoId);
    
    Collection<Todo> findByUserId(Long userId);
    
    Todo saveTodo(Todo todo, Long userId);

	Todo updateTodo(Todo todo, Long id);

	void deleteTodo(Todo todoParam, Long id);

}
