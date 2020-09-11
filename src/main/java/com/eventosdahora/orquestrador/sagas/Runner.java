package com.eventosdahora.orquestrador.sagas;

import com.eventosdahora.orquestrador.sagas.dominio.PedidoState;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoEvent;
import com.eventosdahora.orquestrador.sagas.dominio.Pedido;
import com.eventosdahora.orquestrador.sagas.service.PedidoService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@Log
@Component
public class Runner implements ApplicationRunner {

    @Autowired
    private PedidoService pedidoService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setState(PedidoState.NOVO_PEDIDO);

        pedidoService.novoPedido(pedido);

        pedido.setEvent(PedidoEvent.RESERVA_TICKET_APROVADO);
        StateMachine<PedidoState, PedidoEvent> pagarTicketSM = pedidoService.replyChannel("json", pedido);
        log.info("Após chamar respostaTicket() " + pagarTicketSM.getState().getId().name());
    }
}
