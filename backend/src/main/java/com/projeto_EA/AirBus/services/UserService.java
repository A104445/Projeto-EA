package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.base.BaseService;
import com.projeto_EA.AirBus.models.DTOs.UserDTO;
import com.projeto_EA.AirBus.models.DTOs.UserUpdateDTO;
import com.projeto_EA.AirBus.models.entities.UserEntity;
import com.projeto_EA.AirBus.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService extends BaseService<UserEntity, Long> {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }

    public UserDTO getProfile(Long userID) {
        return userRepository.findById(userID)
                .map(UserDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado!"));
    }

    public UserDTO updateProfile(Long userID, UserUpdateDTO dto) {
        UserEntity user = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado!"));
        if (dto.getNome() != null)           user.setNome(dto.getNome());
        if (dto.getDataNascimento() != null) user.setDataNascimento(dto.getDataNascimento());
        if (dto.getTelefone() != null)       user.setTelefone(dto.getTelefone());
        return new UserDTO(userRepository.save(user));
    }
}