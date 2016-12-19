package com.royasoftware.service;

import com.royasoftware.model.Account;
import com.royasoftware.model.Todo;

import java.util.Collection;

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
