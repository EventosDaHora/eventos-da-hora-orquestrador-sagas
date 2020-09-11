package com.eventosdahora.orquestrador.sagas.repository;

import com.eventosdahora.orquestrador.sagas.dominio.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
