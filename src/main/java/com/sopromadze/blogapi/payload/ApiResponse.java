package com.sopromadze.blogapi.payload;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
	"success",
	"message",
	"code"
})
public class ApiResponse {
    private Boolean success;
    private String message;
    
    public ApiResponse() {
    	
    }

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
