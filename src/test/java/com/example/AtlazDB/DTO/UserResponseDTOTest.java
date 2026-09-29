package com.example.AtlazDB.DTO;

import com.example.AtlazDB.enums.CnhType;
import com.example.AtlazDB.enums.Profile;
import com.example.AtlazDB.enums.UserStatus;
import com.example.AtlazDB.model.User;

import com.example.AtlazDB.dto.UserResponseDTO;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserResponseDTOTest {
    
    @Test
    @DisplayName("Should correctly map all fields from User entity to UserResponseDTO")

    void ShouldMapUserToUserResponseDTO(){
        //Arrange
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(123L);
        when(mockUser.getName()).thenReturn("John Doe");
        when(mockUser.getRegistration()).thenReturn("REG-12345");
        when(mockUser.getEmail()).thenReturn("john.doe@example.com");
        when(mockUser.getProfile()).thenReturn(Profile.TECNICO);
        when(mockUser.getUserStatus()).thenReturn(UserStatus.DISPONIVEL);
        when(mockUser.getCnhTypes()).thenReturn(Set.of(CnhType.B));

        //ACT 
        UserResponseDTO dto = new UserResponseDTO(mockUser);

        //Assert
        assertEquals(123L, dto.id());
        assertEquals("John Doe", dto.name());
        assertEquals("REG-12345", dto.registrationNumber());
        assertEquals("john.doe@example.com", dto.email());
        assertEquals(Profile.TECNICO, dto.profile());
        assertEquals(UserStatus.DISPONIVEL, dto.userStatus());
        assertEquals(Set.of(CnhType.B), dto.cnhTypes());

    }
}
