package com.eventosdahora.orquestrador.sagas.dominio;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
public class Pedido {

    @Id
    @GeneratedValue
    private Long id;

    private PedidoState state;

    private PedidoEvent event;
}
