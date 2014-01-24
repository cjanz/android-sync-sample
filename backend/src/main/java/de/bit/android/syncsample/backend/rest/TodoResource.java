package de.bit.android.syncsample.backend.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.bit.android.syncsample.backend.domain.TodoEntity;
import de.bit.android.syncsample.backend.persistence.TodoRepository;

@Stateless
@Path("/todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {

	@Inject
	private TodoRepository todoRepository;

	@GET
	public List<TodoEntity> getTodos() {
		return todoRepository.getTodos();
	}

}
