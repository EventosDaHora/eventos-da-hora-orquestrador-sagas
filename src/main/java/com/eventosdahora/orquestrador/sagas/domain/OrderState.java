package com.eventosdahora.orquestrador.sagas.domain;

import java.util.Arrays;
import java.util.List;

public enum OrderState {
	NOVO_PEDIDO,
	TICKET_RESERVADO,
	TICKET_RESERVADO_ERRO,
	PAGAMENTO_APROVADO,
	PAGAMENTO_NEGADO,
	TICKET_RESTAURADO,
	TICKET_RESTAURADO_ERRO,
	TICKET_COMPRADO;

	public static boolean isFinalState(OrderState orderState) {
		List<OrderState> finals = Arrays.asList(TICKET_COMPRADO, TICKET_RESTAURADO, TICKET_RESERVADO_ERRO);
		return finals.contains(orderState);
	}
}
