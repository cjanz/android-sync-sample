package de.bit.android.syncsample.backend.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.bit.android.syncsample.backend.domain.TodoEntity;

@Stateless
@Path("/todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {

	@PersistenceContext
	private EntityManager em;

	@GET
	public List<TodoEntity> getTodos() {
		CriteriaQuery<TodoEntity> query = em.getCriteriaBuilder().createQuery(
				TodoEntity.class);
		Root<TodoEntity> root = query.from(TodoEntity.class);
		query.select(root);

		List<TodoEntity> todos = em.createQuery(query).getResultList();
		return todos;
	}

}
