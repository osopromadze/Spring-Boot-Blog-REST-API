package com.sopromadze.blogapi.payload;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class ExceptionResponse {
	private String error;
	private Integer status;
	private List<String> messages;
	private Instant timestamp;

	public ExceptionResponse(List<String> messages, String error, Integer status) {
		setMessages(messages);
		this.error = error;
		this.status = status;
		this.timestamp = Instant.now();
	}

	public List<String> getMessages() {

		return messages == null ? null : new ArrayList<>(messages);
	}

	public final void setMessages(List<String> messages) {

		if (messages == null) {
			this.messages = null;
		} else {
			this.messages = Collections.unmodifiableList(messages);
		}
	}

}
