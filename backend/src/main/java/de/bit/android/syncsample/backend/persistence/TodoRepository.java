package de.bit.android.syncsample.backend.persistence;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.bit.android.syncsample.backend.domain.TodoEntity;

@Stateless
public class TodoRepository {

	@PersistenceContext
	private EntityManager em;

	public List<TodoEntity> getTodos() {
		CriteriaQuery<TodoEntity> query = em.getCriteriaBuilder().createQuery(
				TodoEntity.class);
		Root<TodoEntity> root = query.from(TodoEntity.class);
		query.select(root);

		List<TodoEntity> todos = em.createQuery(query).getResultList();
		return todos;
	}

	public void saveTodo(TodoEntity todo) {
		em.persist(todo);
	}

}
