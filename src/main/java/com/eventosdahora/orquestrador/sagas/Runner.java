package com.eventosdahora.orquestrador.sagas;

import com.eventosdahora.orquestrador.sagas.dto.OrderDTO;
import com.eventosdahora.orquestrador.sagas.dto.OrderState;
import com.eventosdahora.orquestrador.sagas.dto.PaymentDTO;
import com.eventosdahora.orquestrador.sagas.dto.PaymentType;
import com.eventosdahora.orquestrador.sagas.service.OrquestradorPedidoService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Log
@Component
public class Runner implements ApplicationRunner {
	
	@Autowired
	private OrquestradorPedidoService orquestradorPedidoService;
	
	@Override
	public void run(ApplicationArguments args) {
		
//		OrderDTO orderDTO = new OrderDTO();
//		orderDTO.setOrderId(5165656515164L);
//		orderDTO.setOrderState(OrderState.NOVO_PEDIDO);
//		orderDTO.setFees(new BigDecimal(111));
//
//		orderDTO.setPayment(new PaymentDTO());
//		orderDTO.getPayment().setPaymentType(PaymentType.CREDITO);
//		orderDTO.getPayment().setVlAmount(new BigDecimal("100"));
//
//		orquestradorPedidoService.novoPedido(orderDTO);
		
	}
}
