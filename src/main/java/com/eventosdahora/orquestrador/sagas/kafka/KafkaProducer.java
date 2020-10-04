package com.eventosdahora.orquestrador.sagas.kafka;

import com.eventosdahora.orquestrador.sagas.dto.OrderDTO;
import com.eventosdahora.orquestrador.sagas.dto.OrderEvent;
import com.eventosdahora.orquestrador.sagas.dto.OrderState;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Log
@Component
public class KafkaProducer {
	
	@Autowired
	private KafkaTemplate<String, OrderDTO> kafkaTemplate;
	
	@Value(value = "${nome.topico.ticket}")
	private String nomeTopicoTicket;
	
	@Value(value = "${nome.topico.ticket.rollback}")
	private String nomeTopicoTicketRollback;
	
	@Value(value = "${nome.topico.pagamento}")
	private String nomeTopicoPagamento;
	
	public Action<OrderState, OrderEvent> publicaTopicoPagamento(OrderEvent event) {
		return context -> {
			OrderDTO orderDTO = (OrderDTO) context.getMessageHeader(OrderDTO.IDENTIFICADOR);
			orderDTO.setOrderEvent(event);
			publicaTopico(nomeTopicoPagamento, orderDTO);
		};
	}
	
	public Action<OrderState, OrderEvent> publicaTopicoTicket(OrderEvent event) {
		return context -> {
			OrderDTO orderDTO = (OrderDTO) context.getMessageHeader(OrderDTO.IDENTIFICADOR);
			orderDTO.setOrderEvent(event);
			publicaTopico(nomeTopicoTicket, orderDTO);
		};
	}
	
	public Action<OrderState, OrderEvent> publicaTopicoTicketConsolidarCompra(OrderEvent event) {
		return context -> {
			OrderDTO orderDTO = (OrderDTO) context.getMessageHeader(OrderDTO.IDENTIFICADOR);
			orderDTO.setOrderEvent(event);
			publicaTopico(nomeTopicoTicket, orderDTO);
		};
	}
	
	public Action<OrderState, OrderEvent> publicTopicoTicketRollback(OrderEvent event) {
		return context -> {
			OrderDTO orderDTO = (OrderDTO) context.getMessageHeader(OrderDTO.IDENTIFICADOR);
			orderDTO.setOrderEvent(event);
			publicaTopico(nomeTopicoTicketRollback, orderDTO);
		};
	}
	
	private void publicaTopico(String nomeTopico, OrderDTO orderDTO) {
  		log.info("Produzindo mensagem " + orderDTO.toString() + " no tópico " + nomeTopico.toUpperCase());
		ListenableFuture<SendResult<String, OrderDTO>> future = kafkaTemplate.send(nomeTopico, orderDTO);
		
		future.addCallback(new ListenableFutureCallback<>() {
			@Override
			public void onSuccess(SendResult<String, OrderDTO> result) {
				log.info("Pedido enviado: " + orderDTO + "\n Com offset: " + result.getRecordMetadata().offset());
			}
			
			@Override
			public void onFailure(Throwable throwable) {
				log.info("Não foi possível enviar pedido");
			}
		});
	}
}
