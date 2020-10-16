package com.eventosdahora.orquestrador.sagas.controller;

import com.eventosdahora.orquestrador.sagas.dto.OrderDTO;
import com.eventosdahora.orquestrador.sagas.service.OrquestradorPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/orchestrator", produces = { MediaType.APPLICATION_JSON_VALUE})
public class PedidoController {
	
	@Autowired
	private OrquestradorPedidoService orquestradorPedidoService;
	
	@PostMapping("/start-order")
	@ResponseStatus(HttpStatus.OK)
	public void startOrder(@RequestBody OrderDTO orderDTO) {
		orquestradorPedidoService.novoPedido(orderDTO);
	}
	
}
