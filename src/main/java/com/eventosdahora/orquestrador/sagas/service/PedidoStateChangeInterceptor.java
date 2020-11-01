package com.eventosdahora.orquestrador.sagas.service;

import com.eventosdahora.orquestrador.sagas.domain.OrderEvent;
import com.eventosdahora.orquestrador.sagas.domain.OrderState;
import com.eventosdahora.orquestrador.sagas.dto.OrderDTO;
import com.eventosdahora.orquestrador.sagas.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Log
@RequiredArgsConstructor
@Component
public class PedidoStateChangeInterceptor extends StateMachineInterceptorAdapter<OrderState, OrderEvent> {
	
	@Value(value = "${path.order.service}")
	private String pathOrderService;

	@Autowired
	private PedidoRepository repository;

	@Override
	public void preStateChange(State<OrderState, OrderEvent> state,
								Message<OrderEvent> message,
								Transition<OrderState, OrderEvent> transition,
								StateMachine<OrderState, OrderEvent> stateMachine,
								StateMachine<OrderState, OrderEvent> rootStateMachine) {
		Optional.ofNullable(message)
				.flatMap(msg -> Optional.ofNullable(
						(OrderDTO) msg.getHeaders().getOrDefault(OrderDTO.IDENTIFICADOR, -1L)))
				.flatMap(orderDTO -> repository.findById(orderDTO.getOrderId()))
				.ifPresent(pedido -> {
					pedido.setOrderState(state.getId());

					if (OrderState.isFinalState(pedido.getOrderState())) {
						repository.delete(pedido);
					} else {
						repository.update(pedido.getId(), pedido.getOrderState());
					}
				});
	}

	@Override
	public void postStateChange(State<OrderState, OrderEvent> state,
	                            Message<OrderEvent> message,
	                            Transition<OrderState, OrderEvent> transition,
	                            StateMachine<OrderState, OrderEvent> stateMachine,
	                            StateMachine<OrderState, OrderEvent> rootStateMachine) {
		Optional.of(message)
		        .flatMap(msg -> Optional.ofNullable(
				        (OrderDTO) msg.getHeaders().getOrDefault(OrderDTO.IDENTIFICADOR, null)))
		        .ifPresent(pedido -> {
			        pedido.setOrderState(state.getId());

			        if (!OrderState.NOVO_PEDIDO.equals(pedido.getOrderState())) {
						log.info("--- Notificando PEDIDO");
						log.info(pedido.toString());
						RestTemplate client = new RestTemplate();
						client.put(pathOrderService, pedido);
					}
		        });
	}
}
