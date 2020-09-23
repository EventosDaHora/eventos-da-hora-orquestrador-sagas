package com.eventosdahora.orquestrador.sagas.service;

import com.eventosdahora.orquestrador.sagas.dto.OrderDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/pedido")
public interface PedidoController {

    @PostMapping
    void novoPedido(OrderDTO orderDTO);
}
