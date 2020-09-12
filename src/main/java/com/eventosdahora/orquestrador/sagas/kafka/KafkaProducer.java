package com.eventosdahora.orquestrador.sagas.kafka;

import com.eventosdahora.orquestrador.sagas.dominio.Pedido;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoEvent;
import com.eventosdahora.orquestrador.sagas.dominio.PedidoState;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Log
@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, Pedido> kafkaTemplate;

    @Value(value = "${nome.topico.ticket}")
    private String nomeTopicoTicket;

    @Value(value = "${nome.topico.ticket.rollback}")
    private String nomeTopicoTicketRollback;

    @Value(value = "${nome.topico.pagamento}")
    private String nomeTopicoPagamento;

    public Action<PedidoState, PedidoEvent> publicaTopicoPagamento(PedidoEvent event) {
        return context -> {
            Pedido pedido = (Pedido)context.getMessageHeader(Pedido.IDENTIFICADOR);
            pedido.setEvent(event);
            publicaTopico(nomeTopicoPagamento, pedido);
        };
    }

    public Action<PedidoState, PedidoEvent> publicaTopicoTicket(PedidoEvent event) {
        return context -> {
            Pedido pedido = (Pedido)context.getMessageHeader(Pedido.IDENTIFICADOR);
            pedido.setEvent(event);
            publicaTopico(nomeTopicoTicket, pedido);
        };
    }

    public Action<PedidoState, PedidoEvent> publicTopicoTicketRollback(PedidoEvent event) {
        return context -> {
            Pedido pedido = (Pedido)context.getMessageHeader(Pedido.IDENTIFICADOR);
            pedido.setEvent(event);
            publicaTopico(nomeTopicoTicketRollback, pedido);
        };
    }

    private void publicaTopico(String nomeTopico, Pedido pedido) {
        log.info("Produzindo mensagem " + pedido.toString() + " no tópico " + nomeTopico);
        ListenableFuture<SendResult<String, Pedido>> future = kafkaTemplate.send(nomeTopico, pedido);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, Pedido> result) {
                log.info("Pedido enviado: " + pedido + "\n Com offset: " + result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.info("Não foi possível enviar pedido");
            }
        });
    }
}
