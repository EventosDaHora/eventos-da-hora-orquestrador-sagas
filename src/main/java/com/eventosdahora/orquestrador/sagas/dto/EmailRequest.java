package com.eventosdahora.orquestrador.sagas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailRequest {
	
	public String textBody;
	public String email;
	
	public String getTextBody() {
		return textBody;
	}
	
	public void setTextBody(final String textBody) {
		this.textBody = textBody;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(final String email) {
		this.email = email;
	}
}
