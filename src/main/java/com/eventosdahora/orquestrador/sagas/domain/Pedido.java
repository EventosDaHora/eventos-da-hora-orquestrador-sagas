package com.eventosdahora.orquestrador.sagas.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@ToString
public class Pedido {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    public Pedido(Long id, OrderState orderState) {
        this.id = id;
        this.orderState = orderState;
    }

}
