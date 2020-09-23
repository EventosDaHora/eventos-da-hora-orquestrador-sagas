package com.eventosdahora.orquestrador.sagas;

import com.eventosdahora.orquestrador.sagas.dto.OrderDTO;
import com.eventosdahora.orquestrador.sagas.dto.OrderState;
import com.eventosdahora.orquestrador.sagas.service.OrquestradorPedidoService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Log
@Component
public class Runner implements ApplicationRunner {

    @Autowired
    private OrquestradorPedidoService orquestradorPedidoService;

    @Override
    public void run(ApplicationArguments args) {

            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderId(5165656515165L);
            orderDTO.setOrderState(OrderState.NOVO_PEDIDO);
            orquestradorPedidoService.novoPedido(orderDTO);

    }
}
