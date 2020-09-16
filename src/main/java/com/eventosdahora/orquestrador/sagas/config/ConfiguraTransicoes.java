package com.eventosdahora.orquestrador.sagas.config;

import com.eventosdahora.orquestrador.sagas.dominio.PedidoEvent;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoState;
import com.eventosdahora.orquestrador.sagas.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@EnableStateMachineFactory
public class ConfiguraTransicoes extends StateMachineConfig {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Override
    public void configure(StateMachineTransitionConfigurer<PedidoState, PedidoEvent> transitions) throws Exception {
        casosSucesso(transitions);
        casosErro(transitions);
    }

    protected void casosSucesso(StateMachineTransitionConfigurer<PedidoState, PedidoEvent> transitions) throws Exception {
        transitions.withExternal()
                    .source(PedidoState.NOVO_PEDIDO)
                    .target(PedidoState.NOVO_PEDIDO)
                    .event(PedidoEvent.RESERVAR_TICKET)
                    .action(kafkaProducer.publicaTopicoTicket(PedidoEvent.RESERVAR_TICKET))
                    .and()
                .withExternal()
                    .source(PedidoState.NOVO_PEDIDO)
                    .target(PedidoState.TICKET_RESERVADO)
                    .event(PedidoEvent.RESERVA_TICKET_APROVADO)
                    .action(kafkaProducer.publicaTopicoPagamento(PedidoEvent.PAGAR_TICKET))
                    .and()
                .withExternal()
                    .source(PedidoState.TICKET_RESERVADO)
                    .target(PedidoState.PAGAMENTO_APROVADO)
                    .event(PedidoEvent.PAGAR_TICKET_APROVADO)
                    .action(kafkaProducer.publicaTopicoTicket(PedidoEvent.CONSOLIDAR_COMPRA)) // publicar consolidação da compra
                    .and()
                .withExternal()
                    .source(PedidoState.PAGAMENTO_APROVADO)
                    .target(PedidoState.TICKET_COMPRADO)
                    .event(PedidoEvent.CONSOLIDACAO_COMPRA_APROVADO);
    }

    protected void casosErro(StateMachineTransitionConfigurer<PedidoState, PedidoEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(PedidoState.NOVO_PEDIDO)
                    .target(PedidoState.TICKET_RESERVADO_ERRO)
                    .event(PedidoEvent.RESERVA_TICKET_NEGADO)
                    .and()
                .withExternal()
                    .source(PedidoState.TICKET_RESERVADO)
                    .target(PedidoState.PAGAMENTO_NEGADO)
                    .event(PedidoEvent.PAGAR_TICKET_NEGADO)
                    .action(kafkaProducer.publicTopicoTicketRollback(PedidoEvent.RESTAURAR_TICKET))
                    .and()
                .withExternal()
                    .source(PedidoState.PAGAMENTO_NEGADO)
                    .target(PedidoState.TICKET_RESTAURADO)
                    .event(PedidoEvent.TICKET_RESTAURADO_APROVADO)
                    .and()
                .withExternal()
                    .source(PedidoState.PAGAMENTO_NEGADO)
                    .target(PedidoState.TICKET_RESTAURADO_ERRO)
                    .event(PedidoEvent.TICKET_RESTAURADO_NEGADO);
    }
}
