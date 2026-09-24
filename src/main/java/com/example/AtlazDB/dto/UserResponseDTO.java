package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.CnhType;
import com.example.AtlazDB.enums.Profile;
import com.example.AtlazDB.enums.UserStatus;
import com.example.AtlazDB.model.User;

import java.util.Set;

public record UserResponseDTO(

        Long id,
        String name,
        String registrationNumber,
        String email,
        Profile profile,
        UserStatus userStatus,
        Set<CnhType> cnhTypes

) {

    public UserResponseDTO(User user) {

        this(
                user.getId(),
                user.getName(),
                user.getRegistration(),
                user.getEmail(),
                user.getProfile(),
                user.getUserStatus(),
                user.getCnhTypes()
        );
    }
}
