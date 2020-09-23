package com.eventosdahora.orquestrador.sagas.config;

import com.eventosdahora.orquestrador.sagas.dto.OrderEvent;
import com.eventosdahora.orquestrador.sagas.dto.OrderState;
import com.eventosdahora.orquestrador.sagas.kafka.KafkaProducer;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Log
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent>  {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(false)
                .listener(getStateMachineListener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states.withStates()
                .initial(OrderState.NOVO_PEDIDO)
                .states(EnumSet.allOf(OrderState.class))
                .end(OrderState.TICKET_COMPRADO)
                .end(OrderState.TICKET_RESTAURADO)
                .end(OrderState.TICKET_RESERVADO_ERRO);
    }

    protected StateMachineListenerAdapter<OrderState, OrderEvent> getStateMachineListener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
                log.info(String.format("stateChanged(from %s to: %s)", from.getId().name() + "", to.getId().name() + ""));
            }
        };
    }

}
