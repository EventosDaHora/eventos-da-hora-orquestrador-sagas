package com.eventosdahora.orquestrador.sagas.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TicketDTO {

    private Long id;

    private Long ammount;
}
