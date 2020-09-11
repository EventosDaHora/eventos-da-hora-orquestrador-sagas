package com.eventosdahora.orquestrador.sagas.service;

import com.eventosdahora.orquestrador.sagas.dominio.PedidoState;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;


@Log
@RequiredArgsConstructor
@Component
public class PedidoStateChangeInterceptor extends StateMachineInterceptorAdapter<PedidoState, PedidoEvent> {

    @Override
    public void preStateChange(State<PedidoState, PedidoEvent> state,
                                Message<PedidoEvent> message,
                                Transition<PedidoState, PedidoEvent> transition,
                                StateMachine<PedidoState, PedidoEvent> stateMachine,
                                StateMachine<PedidoState, PedidoEvent> rootStateMachine) {

        //TODO: Chamar o serviço de pedido e atualizar o status dele
        log.info("Dentro do interceptador");
        log.info(state.getId().name());
        message.getHeaders().forEach((chave, valor) -> {
            log.info(chave);
            log.info(valor.toString());
        });
    }
}
