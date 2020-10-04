package com.eventosdahora.orquestrador.sagas.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDTO {
	
	public static final String IDENTIFICADOR = "ID_PEDIDO";
	
	private Long orderId;
	
	private LocalDate createdDate;
	
	private OrderState orderState;
	
	private OrderEvent orderEvent;
	
	private BigDecimal fees;
	
	private Long userId;
	
	@Builder.Default
	private List<TicketDTO> tickets = new ArrayList<>();
	
	private PaymentDTO payment;

}
