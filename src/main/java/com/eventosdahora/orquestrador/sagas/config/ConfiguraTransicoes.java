package com.eventosdahora.orquestrador.sagas.config;

import com.eventosdahora.orquestrador.sagas.dto.OrderEvent;
import com.eventosdahora.orquestrador.sagas.dto.OrderState;
import com.eventosdahora.orquestrador.sagas.kafka.KafkaProducer;
import com.eventosdahora.orquestrador.sagas.service.OrquestradorPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@EnableStateMachineFactory
public class ConfiguraTransicoes extends StateMachineConfig {
	
	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Autowired
	private OrquestradorPedidoService orquestradorPedidoService;
	
	@Override
	public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
		casosSucesso(transitions);
		casosErro(transitions);
	}
	
	protected void casosSucesso(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
		transitions.withExternal()
		           .source(OrderState.NOVO_PEDIDO)
		           .target(OrderState.NOVO_PEDIDO)
		           .event(OrderEvent.RESERVAR_TICKET)
		           .action(kafkaProducer.publicaTopicoTicket(OrderEvent.RESERVAR_TICKET))
		           .and()
		           .withExternal()
		           .source(OrderState.NOVO_PEDIDO)
		           .target(OrderState.TICKET_RESERVADO)
		           .event(OrderEvent.RESERVA_TICKET_APROVADO)
		           .action(kafkaProducer.publicaTopicoPagamento(OrderEvent.PAGAR_TICKET))
		           .and()
		           .withExternal()
		           .source(OrderState.NOVO_PEDIDO)
		           .target(OrderState.PAGAMENTO_APROVADO)
		           .event(OrderEvent.PAGAR_TICKET_APROVADO)
		           .action(kafkaProducer.publicaTopicoTicketConsolidarCompra(
				           OrderEvent.CONSOLIDAR_COMPRA)) // publicar consolidação da compra
		           .and()
		           .withExternal()
		           .source(OrderState.NOVO_PEDIDO)
		           .target(OrderState.TICKET_COMPRADO)
		           .event(OrderEvent.CONSOLIDACAO_COMPRA_APROVADO)
		           .action(orquestradorPedidoService.notifyOrderService(OrderEvent.CONSOLIDACAO_COMPRA_APROVADO));
	}
	
	protected void casosErro(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
		transitions
				.withExternal()
				.source(OrderState.NOVO_PEDIDO)
				.target(OrderState.TICKET_RESERVADO_ERRO)
				.event(OrderEvent.RESERVA_TICKET_NEGADO)
				.and()
				.withExternal()
				.source(OrderState.NOVO_PEDIDO)
				.target(OrderState.PAGAMENTO_NEGADO)
				.event(OrderEvent.PAGAR_TICKET_NEGADO)
				.action(kafkaProducer.publicTopicoTicketRollback(OrderEvent.RESTAURAR_TICKET))
				.and()
				.withExternal()
				.source(OrderState.NOVO_PEDIDO)
				.target(OrderState.TICKET_RESTAURADO)
				.event(OrderEvent.TICKET_RESTAURADO_APROVADO)
				.and()
				.withExternal()
				.source(OrderState.NOVO_PEDIDO)
				.target(OrderState.TICKET_RESTAURADO_ERRO)
				.event(OrderEvent.TICKET_RESTAURADO_NEGADO);
	}
}
