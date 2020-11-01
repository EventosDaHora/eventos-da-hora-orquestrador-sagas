package com.eventosdahora.orquestrador.sagas.service;

import com.eventosdahora.orquestrador.sagas.domain.OrderEvent;
import com.eventosdahora.orquestrador.sagas.domain.OrderState;
import com.eventosdahora.orquestrador.sagas.domain.Pedido;
import com.eventosdahora.orquestrador.sagas.dto.OrderDTO;
import com.eventosdahora.orquestrador.sagas.repository.PedidoRepository;
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
public class OrquestradorPedidoService {

    @Autowired
    private StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;

    @Autowired
    private PedidoStateChangeInterceptor pedidoStateInterceptor;

    @Autowired
    private PedidoRepository repository;

    public void novoPedido(OrderDTO orderDTO) {
        log.info("--- Recebendo Novo Pedido");
        log.info(orderDTO.toString());
        orderDTO.setOrderEvent(OrderEvent.RESERVAR_TICKET);

        Pedido pedido = new Pedido(orderDTO.getOrderId(), orderDTO.getOrderState());
        repository.save(pedido);

        StateMachine<OrderState, OrderEvent> sm = build(orderDTO);
        sendEvent(orderDTO, sm);
    }

    @KafkaListener(topics = "${nome.topico.reply.channel}", containerFactory = "pedidoKafkaListenerContainerFactory")
    public StateMachine<OrderState, OrderEvent> replyChannel(OrderDTO orderDTO) {
        log.info("Pedido recebido do tópico reply-channel " + orderDTO);
        StateMachine<OrderState, OrderEvent> sm = build(orderDTO);
        sendEvent(orderDTO, sm);
        return sm;
    }

    private void sendEvent(OrderDTO orderDTO, StateMachine<OrderState, OrderEvent> sm) {
        Message<OrderEvent> msg = MessageBuilder.withPayload(orderDTO.getOrderEvent())
                .setHeader(OrderDTO.IDENTIFICADOR, orderDTO)
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<OrderState, OrderEvent> build(OrderDTO orderDTO) {
        return repository.findById(orderDTO.getOrderId()) //
                .map(this::getStateMachine) //
                .orElseGet(null);
    }

    private StateMachine<OrderState, OrderEvent> getStateMachine(Pedido pedido) {
        StateMachine<OrderState, OrderEvent> sm = stateMachineFactory.getStateMachine(pedido.getId().toString());

        sm.stop();

        // Altera estado da máquina
        sm.getStateMachineAccessor() //
                .doWithAllRegions(sma -> { //
                    sma.addStateMachineInterceptor(pedidoStateInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(pedido.getOrderState(), null, null, null));
                });

        sm.start();

        return sm;
    }
}
