package de.bit.android.syncsample.backend.persistence;

import static de.bit.android.syncsample.backend.domain.QTodoEntity.todoEntity;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
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

	public TodoEntity saveTodo(TodoEntity todo) throws UpdateConflictException {
		try {
			if (todo.getId() != null) {
				todo = em.merge(todo);
			}
			em.persist(todo);
		} catch (OptimisticLockException e) {
			em.clear();
			throw new UpdateConflictException(e);
		}
		return todo;
	}

	public void deleteTodo(Long id) {
		TodoEntity entity = findOne(id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	public TodoEntity findOne(Long id) {
		return em.find(TodoEntity.class, id);
	}

}
