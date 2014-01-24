package de.bit.android.syncsample.backend.domain;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import de.bit.android.syncsample.backend.persistence.TodoRepository;

@Singleton
@Startup
public class TestDataGenerator {

	@Inject
	private TodoRepository todoRepository;

	@PostConstruct
	public void createTestData() {
		for (int i = 0; i < 10; i++) {
			TodoEntity todo = new TodoEntity();
			todo.setTitle("Todo: " + i);
			todo.setText("Lorem Ipsum");

			todoRepository.saveTodo(todo);
		}
	}

}
