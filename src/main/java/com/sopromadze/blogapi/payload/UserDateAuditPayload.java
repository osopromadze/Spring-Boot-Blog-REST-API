package com.sopromadze.blogapi.payload;

import lombok.Data;

@Data
public abstract class UserDateAuditPayload extends DateAuditPayload {
	private Long createdBy;

	private Long updatedBy;

}
