package de.bit.android.syncsample.rest;

import java.io.IOException;

import de.bit.android.syncsample.content.TodoEntity;

public class UpdateConflictException extends IOException {

	private static final long serialVersionUID = -3542414867349901503L;

	private final TodoEntity conflictedEntity;

	public UpdateConflictException(TodoEntity conflictedEntity) {
		super();
		this.conflictedEntity = conflictedEntity;
	}

	public TodoEntity getConflictedEntity() {
		return conflictedEntity;
	}

}
