package com.projeto_EA.AirBus.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificacaoDTO {
    private String tipo;
    private String mensagem;
    private Long tituloID;
}
