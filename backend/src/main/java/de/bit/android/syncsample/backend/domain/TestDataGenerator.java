package de.bit.android.syncsample.backend.domain;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@Startup
public class TestDataGenerator {

	@PersistenceContext
	private EntityManager em;

	@PostConstruct
	public void createTestData() {
		for (int i = 0; i < 10; i++) {
			TodoEntity todo = new TodoEntity();
			todo.setTitle("Todo: " + i);
			todo.setText("Lorem Ipsum");

			saveTodo(todo);
		}
	}

	public void saveTodo(TodoEntity todo) {
		em.persist(todo);
	}

}
