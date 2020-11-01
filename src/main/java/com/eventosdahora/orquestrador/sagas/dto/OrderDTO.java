package com.eventosdahora.orquestrador.sagas.dto;

import com.eventosdahora.orquestrador.sagas.domain.OrderEvent;
import com.eventosdahora.orquestrador.sagas.domain.OrderState;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDTO {
	
	public static final String IDENTIFICADOR = "ID_PEDIDO";
	
	private Long orderId;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdDate;
	
	private OrderState orderState;
	
	private OrderEvent orderEvent;
	
	private BigDecimal fees;
	
	private Long userId;

	private String emailNotification;
	
	@Builder.Default
	private List<TicketDTO> tickets = new ArrayList<>();
	
	private PaymentDTO payment;

}
