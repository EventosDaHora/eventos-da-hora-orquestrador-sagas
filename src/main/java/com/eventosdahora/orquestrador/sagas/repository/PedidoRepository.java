package com.eventosdahora.orquestrador.sagas.repository;

import com.eventosdahora.orquestrador.sagas.domain.OrderState;
import com.eventosdahora.orquestrador.sagas.domain.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Pedido p SET p.orderState = :orderState WHERE p.id = :id")
    void update(@Param("id") Long id, @Param("orderState") OrderState orderState);
}
