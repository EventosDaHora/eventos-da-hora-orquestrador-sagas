package com.eventosdahora.orquestrador.sagas.config;

import com.eventosdahora.orquestrador.sagas.dominio.PedidoEvent;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoState;
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
public class StateMachineConfig extends StateMachineConfigurerAdapter<PedidoState, PedidoEvent>  {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Override
    public void configure(StateMachineConfigurationConfigurer<PedidoState, PedidoEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(false)
                .listener(getStateMachineListener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<PedidoState, PedidoEvent> states) throws Exception {
        states.withStates()
                .initial(PedidoState.NOVO_PEDIDO)
                .states(EnumSet.allOf(PedidoState.class))
                .end(PedidoState.TICKET_COMPRADO)
                .end(PedidoState.TICKET_RESTAURADO)
                .end(PedidoState.TICKET_RESERVADO_ERRO);
    }

    protected StateMachineListenerAdapter<PedidoState, PedidoEvent> getStateMachineListener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PedidoState, PedidoEvent> from, State<PedidoState, PedidoEvent> to) {
                log.info(String.format("stateChanged(from %s to: %s)", from.getId().name() + "", to.getId().name() + ""));
            }
        };
    }

}
