package de.bit.android.syncsample.backend.persistence;

import static de.bit.android.syncsample.backend.domain.QTodoEntity.todoEntity;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.mysema.query.jpa.impl.JPAQuery;

import de.bit.android.syncsample.backend.domain.TodoEntity;

@Stateless
public class TodoRepository {

	@PersistenceContext
	private EntityManager em;

	public List<TodoEntity> getTodos() {
		return query().from(todoEntity).orderBy(todoEntity.title.asc())
				.list(todoEntity);
	}

	private JPAQuery query() {
		return new JPAQuery(em);
	}

	public void saveTodo(TodoEntity todo) {
		em.persist(todo);
	}

}
