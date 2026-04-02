package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
public class UserDTO {
    private Long ID;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String telefone;

    public UserDTO(UserEntity u) {
        this.ID = u.getID();
        this.nome = u.getNome();
        this.email = u.getEmail();
        this.dataNascimento = u.getDataNascimento();
        this.telefone = u.getTelefone();
    }
}