package com.eventosdahora.orquestrador.sagas.kafka;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Log
@Component
public class KafkaProducer {
/*
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void enviaMensagem(String mensagem, String topico) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topico, mensagem);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Enviando mensagem: " + mensagem + " com offset: " + result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.info("Não foi possível enviar mensagem");
            }
        });
    }*/
}
