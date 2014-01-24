package de.bit.android.syncsample.backend.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.bit.android.syncsample.backend.domain.TodoEntity;
import de.bit.android.syncsample.backend.persistence.TodoRepository;

@RequestScoped
@Named
public class CreateTodoController {

	private TodoEntity todo;

	@Inject
	private TodoRepository todoRepository;

	@PostConstruct
	public void init() {
		todo = new TodoEntity();
	}

	public TodoEntity getTodo() {
		return todo;
	}

	public String createTodo() {

		todoRepository.saveTodo(todo);
		init();

		return "index";
	}

}
