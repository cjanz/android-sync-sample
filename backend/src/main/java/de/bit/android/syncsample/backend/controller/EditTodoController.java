package de.bit.android.syncsample.backend.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.bit.android.syncsample.backend.domain.TodoEntity;
import de.bit.android.syncsample.backend.persistence.TodoRepository;

@RequestScoped
@Named
public class EditTodoController {

	private TodoEntity todo;

	private Long todoId;

	@Inject
	private TodoRepository todoRepository;

	@PostConstruct
	public void init() {
		todo = new TodoEntity();
	}

	private void loadTodo() {
		if (todoId != null) {
			todo = todoRepository.findOne(todoId);
		}
	}

	public TodoEntity getTodo() {
		return todo;
	}

	public String saveTodo() {

		todoRepository.saveTodo(todo);

		return "index?faces-redirect=true";
	}

	public Long getTodoId() {
		return todoId;
	}

	public void setTodoId(Long todoId) {
		this.todoId = todoId;
		loadTodo();
	}

}
