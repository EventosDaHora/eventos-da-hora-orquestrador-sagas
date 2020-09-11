package com.eventosdahora.orquestrador.sagas.config;

import com.eventosdahora.orquestrador.sagas.constants.Constants;
import com.eventosdahora.orquestrador.sagas.dominio.Pedido;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoEvent;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoState;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Log
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PedidoState, PedidoEvent>  {

    //@Autowired
    //private KafkaProducer kafkaProducer;

    /**
     * // Configura a engine do statemachine
     * @param config
     * @throws Exception
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<PedidoState, PedidoEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(false)
                .listener(getStateMachineListener());
    }

    /**
     * Configura os estados da máquina
     * @param states
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<PedidoState, PedidoEvent> states) throws Exception {
        states.withStates()
                .initial(PedidoState.NOVO_PEDIDO)
                .states(EnumSet.allOf(PedidoState.class))
                .end(PedidoState.TICKET_COMPRADO)
                .end(PedidoState.TICKET_RESTAURADO)
                .end(PedidoState.TICKET_RESERVADO_ERRO);
    }

    protected Action<PedidoState, PedidoEvent> publicaTopicoPagamento() {
        return context -> {
            Pedido pedido = (Pedido)context.getMessageHeader(Constants.PEDIDO_ENTITY.getTexto());
            log.info("Produzindo mensagem " + pedido.toString() + " no tópico executa-pagamento");
            //kafkaProducer.enviaMensagem(pedido.toString(), "executa-pagamento");
        };
    }

    /**
     * Ação a ser executada quando entrar num determinado estado
     * @return
     */
    protected Action<PedidoState, PedidoEvent> publicaTopicoTicket() {
        return context -> {
            Pedido pedido = (Pedido)context.getMessageHeader(Constants.PEDIDO_ENTITY.getTexto());
            log.info("Produzindo mensagem" + pedido.toString() +" no tópico executa-reserva-ticket");
            //kafkaProducer.enviaMensagem("bla bla bla", "executa-reserva-ticket");
        };
    }

    protected Action<PedidoState, PedidoEvent> publicTopicoTicketRollback() {
        return null;
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
