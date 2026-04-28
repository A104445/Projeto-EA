package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.UserDTO;
import com.projeto_EA.AirBus.models.DTOs.UserUpdateDTO;
import com.projeto_EA.AirBus.models.entities.UserEntity;
import com.projeto_EA.AirBus.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDTO getProfile(Long userID) {
        return userRepository.findById(userID)
                .map(UserDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado!"));
    }

    @Transactional
    public UserDTO updateProfile(Long userID, UserUpdateDTO dto) {
        UserEntity user = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado!"));
        if (dto.getNome() != null)           user.setNome(dto.getNome());
        if (dto.getDataNascimento() != null) user.setDataNascimento(dto.getDataNascimento());
        if (dto.getTelefone() != null)       user.setTelefone(dto.getTelefone());
        return new UserDTO(userRepository.save(user));
    }
}