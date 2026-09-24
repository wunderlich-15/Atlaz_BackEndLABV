package com.example.AtlazDB.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.AtlazDB.dto.UserRequestDTO;
import com.example.AtlazDB.enums.UserStatus;
import com.example.AtlazDB.model.User;
import com.example.AtlazDB.repository.ServiceOrderRepository;
import com.example.AtlazDB.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;
    private final ServiceOrderRepository serviceOrderRepository;

    public UserService(UserRepository repository, ServiceOrderRepository serviceOrderRepository) {
        this.repository = repository;
        this.serviceOrderRepository = serviceOrderRepository;
    }


    public List<User> listAll() {
        return repository.findAll();
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public User save(UserRequestDTO dto) {

        User user = new User();

        user.setName(dto.getName());
        user.setRegistration(dto.getRegistrationNumber());
        user.setPasswordHash(dto.getPasswordHash());
        user.setEmail(dto.getEmail());
        user.setProfile(dto.getProfile());
        if (dto.getUserStatus() != null) {
        user.setUserStatus(UserStatus.valueOf(dto.getUserStatus()));
        } else {
        user.setUserStatus(UserStatus.DISPONIVEL);
        }
        user.setCnhTypes(dto.getCnhTypes());

        return repository.save(user);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

public User update(Long id, UserRequestDTO dto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getUserStatus() != null && UserStatus.valueOf(dto.getUserStatus()) == UserStatus.DESLIGADO) { 
            
            boolean hasActiveOrder = serviceOrderRepository.existsByUserIdAndReturnDateIsNull(id);
            if (hasActiveOrder) {
                throw new BusinessRuleException("Não é possível inativar o técnico porque ele está vinculado a uma Ordem de Serviço em andamento.");
            }
        }
        user.setName(dto.getName());
        user.setRegistration(dto.getRegistrationNumber());
        user.setPasswordHash(dto.getPasswordHash());
        user.setEmail(dto.getEmail());
        user.setProfile(dto.getProfile());
        if (dto.getUserStatus() != null) {
        user.setUserStatus(UserStatus.valueOf(dto.getUserStatus()));
        }
        user.setCnhTypes(dto.getCnhTypes());

        return repository.save(user);
    }
}