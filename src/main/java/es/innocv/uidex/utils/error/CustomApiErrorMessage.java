package es.innocv.uidex.utils.error;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class CustomApiErrorMessage {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
	
    private HttpStatus status;
    
    private String error;
    
    @JsonInclude(Include.NON_NULL)
    private String debugMessage;

    private CustomApiErrorMessage(CustomApiErrorMessageBuilder builder) {
    	this.timestamp = LocalDateTime.now();
		this.status = builder.status;
		this.error = builder.error;
		this.debugMessage = builder.debugMessage;
    }
    
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	public String getDebugMessage() {
		return debugMessage;
	}
	
	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}
    
	public static class CustomApiErrorMessageBuilder {
	    private HttpStatus status;
	    private String error;
	    private String debugMessage;
	    
	    public CustomApiErrorMessageBuilder setStatus(HttpStatus status) {
	    	this.status = status;
	    	return this;
	    }
	    
	    public CustomApiErrorMessageBuilder setError(String error) {
	    	this.error = error;
	    	return this;
	    }
	    
	    public CustomApiErrorMessageBuilder setDebugMessage(String debugMessage) {
	    	this.debugMessage = debugMessage;
	    	return this;
	    }
	    
	    public CustomApiErrorMessage build() {
	    	return new CustomApiErrorMessage(this);
	    }
		
	}
}
