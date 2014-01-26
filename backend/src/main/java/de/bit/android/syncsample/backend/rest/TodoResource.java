package de.bit.android.syncsample.backend.rest;

import static com.google.common.base.Preconditions.checkState;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.UriBuilder.fromUri;

import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.bit.android.syncsample.backend.domain.TodoEntity;
import de.bit.android.syncsample.backend.persistence.TodoRepository;

@Stateless
@Path("/todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {

	@Context
	private UriInfo uriInfo;

	@Inject
	private TodoRepository todoRepository;

	@GET
	public List<TodoEntity> getTodos() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return todoRepository.getTodos();
	}

	@GET
	@Path("{id}")
	public Response getTodo(@PathParam("id") Long id) {
		TodoEntity entity = todoRepository.findOne(id);
		if (entity == null) {
			return Response.status(NOT_FOUND).build();
		}

		return Response.ok(entity).build();
	}

	@POST
	public Response createTodo(TodoEntity todo) {
		TodoEntity savedTodo = todoRepository.saveTodo(todo);

		return Response
				.created(
						fromUri(uriInfo.getRequestUri()).path("{id}").build(
								todo.getId())).entity(savedTodo).build();
	}

	@PUT
	@Path("{id}")
	public Response updateTodo(TodoEntity todo, @PathParam("id") Long id) {
		if (todo.getVersion() == null) {
			throw new WebApplicationException(BAD_REQUEST);
		}

		if (todoRepository.findOne(id) == null) {
			return Response.status(NOT_FOUND).build();
		}

		if (todo.getId() == null) {
			todo.setId(id);
		} else {
			checkState(Objects.equals(id, todo.getId()), "IDs don't match");
		}

		TodoEntity savedTodo = todoRepository.saveTodo(todo);
		return Response.ok(savedTodo).build();
	}

	@DELETE
	@Path("{id}")
	public void deleteTodo(@PathParam("id") Long id) {
		todoRepository.deleteTodo(id);
	}

}
