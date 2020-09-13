package com.eventosdahora.orquestrador.sagas;

import com.eventosdahora.orquestrador.sagas.dominio.PedidoState;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoEvent;
import com.eventosdahora.orquestrador.sagas.dominio.Pedido;
import com.eventosdahora.orquestrador.sagas.service.OrquestradorPedidoService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Log
@Component
public class Runner implements ApplicationRunner {

    @Autowired
    private OrquestradorPedidoService orquestradorPedidoService;

    @Override
    public void run(ApplicationArguments args) {

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setState(PedidoState.NOVO_PEDIDO);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
            orquestradorPedidoService.novoPedido(pedido);
        }, 500, 100, TimeUnit.MILLISECONDS);



    }
}
