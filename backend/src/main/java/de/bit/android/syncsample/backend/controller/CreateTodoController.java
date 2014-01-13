package de.bit.android.syncsample.backend.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.bit.android.syncsample.backend.domain.TestDataGenerator;
import de.bit.android.syncsample.backend.domain.TodoEntity;

@RequestScoped
@Named
public class CreateTodoController {

	private TodoEntity todo;

	@Inject
	private TestDataGenerator testDataGenerator;

	@PostConstruct
	public void init() {
		todo = new TodoEntity();
	}

	public TodoEntity getTodo() {
		return todo;
	}

	public String createTodo() {

		testDataGenerator.saveTodo(todo);
		init();

		return null;
	}

}
