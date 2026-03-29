package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.base.BaseService;
import com.projeto_EA.AirBus.models.dtos.UserDTO;
import com.projeto_EA.AirBus.models.entities.UserEntity;
import com.projeto_EA.AirBus.models.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<UserEntity, Long> {

    @Autowired private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        super(userRepository);
    }

    public UserDTO findById(Long id) {
        return userRepository.findById(id).map(UserDTO::new).orElse(null);
    }

    public UserDTO save(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userDTO.getName());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setPassword(userDTO.getPassword());
        return new UserDTO(userRepository.save(userEntity));
    }
}
