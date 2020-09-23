package com.eventosdahora.orquestrador.sagas.service;

import com.eventosdahora.orquestrador.sagas.dto.OrderDTO;
import com.eventosdahora.orquestrador.sagas.dto.OrderEvent;
import com.eventosdahora.orquestrador.sagas.dto.OrderState;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Log
@RequiredArgsConstructor
@Service
public class OrquestradorPedidoService implements PedidoController {

    @Autowired
    private StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;

    @Autowired
    private PedidoStateChangeInterceptor pedidoStateInterceptor;

    @Override
    public void novoPedido(OrderDTO orderDTO) {
        orderDTO.setOrderEvent(OrderEvent.RESERVAR_TICKET);
        StateMachine<OrderState, OrderEvent> sm = build(orderDTO);
        sendEvent(orderDTO, sm, orderDTO.getOrderEvent());
    }

    @KafkaListener(topics = "${nome.topico.reply.channel}",
            containerFactory = "pedidoKafkaListenerContainerFactory")
    public StateMachine<OrderState, OrderEvent> replyChannel(OrderDTO orderDTO) {
        log.info("Pedido recebido do tópico reply-channel " + orderDTO);
        StateMachine<OrderState, OrderEvent> sm = build(orderDTO);
        sendEvent(orderDTO, sm, orderDTO.getOrderEvent());
        return sm;
    }

    private void sendEvent(OrderDTO orderDTO, StateMachine<OrderState, OrderEvent> sm, OrderEvent event) {
        Message<OrderEvent> msg = MessageBuilder.withPayload(event)
                .setHeader(OrderDTO.IDENTIFICADOR, orderDTO)
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<OrderState, OrderEvent> build(OrderDTO orderDTO) {

        StateMachine<OrderState, OrderEvent> sm = stateMachineFactory.getStateMachine(orderDTO.getOrderId().toString());

        sm.stop();

        // Altera estado da máquina
        sm.getStateMachineAccessor()
          .doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(pedidoStateInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(orderDTO.getOrderState(), null, null, null));
          });

        sm.start();

        return sm;
    }
}
