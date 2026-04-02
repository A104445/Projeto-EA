package com.projeto_EA.AirBus.models.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class UserUpdateDTO {
    private String nome;
    private LocalDate dataNascimento;
    private String telefone;
}