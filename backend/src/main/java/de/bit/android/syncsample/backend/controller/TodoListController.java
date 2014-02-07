package de.bit.android.syncsample.backend.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.bit.android.syncsample.backend.domain.TodoEntity;
import de.bit.android.syncsample.backend.persistence.TodoRepository;

@Named
@RequestScoped
public class TodoListController {

	@Inject
	private TodoRepository todoRepository;

	private List<TodoEntity> todos = new ArrayList<>();

	@PostConstruct
	public void init() {
		todos = todoRepository.getTodos();
	}

	public List<TodoEntity> getTodos() {
		return todos;
	}
	
	public String deleteTodo(TodoEntity todo) {
		todoRepository.deleteTodo(todo.getId());
		
		return "index?faces-redirect=true";
	}

}
