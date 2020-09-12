package com.eventosdahora.orquestrador.sagas.dominio;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pedido {

    public static final String IDENTIFICADOR = "ID_PEDIDO";

    private Long id;

    private PedidoState state;

    private PedidoEvent event;
}
