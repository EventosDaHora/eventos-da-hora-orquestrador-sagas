package com.eventosdahora.orquestrador.sagas.service;

import com.eventosdahora.orquestrador.sagas.dto.OrderDTO;
import com.eventosdahora.orquestrador.sagas.dto.OrderEvent;
import com.eventosdahora.orquestrador.sagas.dto.OrderState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;
import java.util.Optional;

@Log
@RequiredArgsConstructor
@Component
public class PedidoStateChangeInterceptor extends StateMachineInterceptorAdapter<OrderState, OrderEvent> {
	
	@Value(value = "${path.order.service}")
	private String pathOrderService;
	
	@Override
	public void postStateChange(State<OrderState, OrderEvent> state,
	                            Message<OrderEvent> message,
	                            Transition<OrderState, OrderEvent> transition,
	                            StateMachine<OrderState, OrderEvent> stateMachine,
	                            StateMachine<OrderState, OrderEvent> rootStateMachine) {
		
		log.info("Dentro do interceptador");
		log.info("Estado: " + state.getId().name());
		
		Optional.of(message)
		        .flatMap(msg -> Optional.ofNullable(
				        (OrderDTO) msg.getHeaders().getOrDefault(OrderDTO.IDENTIFICADOR, null)))
		        .ifPresent(pedido -> {
			        pedido.setOrderState(state.getId());
			        //TODO: Notificar o serviço de pedido sobre o novo estado
			        RestTemplate client = new RestTemplate();
			        log.info("--- Notificando PEDIDO");
			        log.info(pedido.toString());
			        ObjectMapper ob = new ObjectMapper();
			        try {
				        log.info(ob.writeValueAsString(pedido));
			        } catch (JsonProcessingException e) {
				        e.printStackTrace();
			        }
			        client.put(pathOrderService, pedido);
		        });
	}
}
