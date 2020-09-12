package com.eventosdahora.orquestrador.sagas.service;

import com.eventosdahora.orquestrador.sagas.dominio.Pedido;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoEvent;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoState;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log
@RequiredArgsConstructor
@Component
public class PedidoStateChangeInterceptor extends StateMachineInterceptorAdapter<PedidoState, PedidoEvent> {

    @Override
    public void postStateChange(State<PedidoState, PedidoEvent> state,
                                Message<PedidoEvent> message,
                                Transition<PedidoState, PedidoEvent> transition,
                                StateMachine<PedidoState, PedidoEvent> stateMachine,
                                StateMachine<PedidoState, PedidoEvent> rootStateMachine) {

        log.info("Dentro do interceptador");
        log.info("Estado: " + state.getId().name());

        Optional.of(message)
                .flatMap(msg -> Optional.ofNullable(
                        (Pedido) msg.getHeaders().getOrDefault(Pedido.IDENTIFICADOR, null)))
                .ifPresent(pedido -> {
                    pedido.setState(state.getId());
                    //TODO: Notificar o serviço de pedido sobre o novo estado
                });
    }
}
