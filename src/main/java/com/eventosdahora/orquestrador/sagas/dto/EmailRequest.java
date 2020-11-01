package com.eventosdahora.orquestrador.sagas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailRequest {
	
	public String textBody;

	public String email;
	
}
