package com.eventosdahora.orquestrador.sagas.service;

import com.eventosdahora.orquestrador.sagas.dominio.Pedido;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoEvent;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class OrquestradorPedidoService implements PedidoController {

    @Autowired
    private StateMachineFactory<PedidoState, PedidoEvent> stateMachineFactory;

    @Autowired
    private PedidoStateChangeInterceptor pedidoStateInterceptor;

    @Transactional
    @Override
    public void novoPedido(Pedido pedido) {
        pedido.setEvent(PedidoEvent.RESERVAR_TICKET);
        StateMachine<PedidoState, PedidoEvent> sm = build(pedido);
        sendEvent(pedido, sm, pedido.getEvent());
    }

    @Transactional
    @KafkaListener(topics = "${nome.topico.reply.channel}")
    public StateMachine<PedidoState, PedidoEvent> replyChannel(Pedido pedido){
        StateMachine<PedidoState, PedidoEvent> sm = build(pedido);
        sendEvent(pedido, sm, pedido.getEvent());
        return sm;
    }

    private void sendEvent(Pedido pedido, StateMachine<PedidoState, PedidoEvent> sm, PedidoEvent event) {
        Message<PedidoEvent> msg = MessageBuilder.withPayload(event)
                .setHeader(Pedido.IDENTIFICADOR, pedido)
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<PedidoState, PedidoEvent> build(Pedido pedido) {

        StateMachine<PedidoState, PedidoEvent> sm = stateMachineFactory.getStateMachine(pedido.getId().toString());

        sm.stop();

        // Altera estado da máquina
        sm.getStateMachineAccessor()
          .doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(pedidoStateInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(pedido.getState(), null, null, null));
          });

        sm.start();

        return sm;
    }
}
