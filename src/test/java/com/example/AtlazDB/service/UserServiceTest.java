package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.UserRequestDTO;
import com.example.AtlazDB.enums.CnhType;
import com.example.AtlazDB.enums.Profile;
import com.example.AtlazDB.enums.UserStatus;
import com.example.AtlazDB.service.BusinessRuleException;
import com.example.AtlazDB.model.User;
import com.example.AtlazDB.repository.ServiceOrderRepository;
import com.example.AtlazDB.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setRegistration("REG-12345");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setPasswordHash("hashed_password");
        mockUser.setProfile(Profile.ADMIN);
        mockUser.setUserStatus(UserStatus.DISPONIVEL);
        mockUser.setCnhTypes(Set.of(CnhType.B));
    }

    @Nested
    @DisplayName("Tests for listAll and findById")
    class ReadOperationsTests {

        @Test
        @DisplayName("Should return all users")
        void shouldReturnAllUsers() {
            when(repository.findAll()).thenReturn(List.of(mockUser));

            List<User> result = userService.listAll();

            assertEquals(1, result.size());
            assertEquals("John Doe", result.get(0).getName());
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return Optional containing user when found by ID")
        void shouldReturnUserWhenFoundById() {
            when(repository.findById(1L)).thenReturn(Optional.of(mockUser));

            Optional<User> result = userService.findById(1L);

            assertTrue(result.isPresent());
            assertEquals(1L, result.get().getId());
            verify(repository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return empty Optional when user not found by ID")
        void shouldReturnEmptyOptionalWhenUserNotFound() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            Optional<User> result = userService.findById(1L);

            assertTrue(result.isEmpty());
            verify(repository, times(1)).findById(1L);
        }
    }

    @Nested
    @DisplayName("Tests for save")
    class SaveOperationsTests {

        @Test
        @DisplayName("Should save user with provided user status")
        void shouldSaveUserWithProvidedStatus() {
            UserRequestDTO dto = new UserRequestDTO();
            dto.setName("Jane Doe");
            dto.setRegistrationNumber("REG-67890");
            dto.setEmail("jane.doe@example.com");
            dto.setPasswordHash("secret");
            dto.setProfile(Profile.TECNICO);
            dto.setUserStatus("DESLIGADO");
            dto.setCnhTypes(Set.of(CnhType.A, CnhType.B));

            when(repository.save(any(User.class))).thenAnswer(i -> {
                User saved = i.getArgument(0);
                saved.setId(2L);
                return saved;
            });

            User result = userService.save(dto);

            assertNotNull(result);
            assertEquals(2L, result.getId());
            assertEquals("Jane Doe", result.getName());
            assertEquals(UserStatus.DESLIGADO, result.getUserStatus());
            verify(repository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Should save user with default DISPONIVEL status when status is null")
        void shouldSaveUserWithDefaultStatusWhenStatusIsNull() {
            UserRequestDTO dto = new UserRequestDTO();
            dto.setName("Jane Doe");
            dto.setUserStatus(null); // Status is null

            when(repository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

            User result = userService.save(dto);

            assertNotNull(result);
            assertEquals(UserStatus.DISPONIVEL, result.getUserStatus());
            verify(repository, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Tests for delete and update")
    class MutationOperationsTests {

        @Test
        @DisplayName("Should delete user by ID")
        void shouldDeleteUserById() {
            userService.delete(1L);
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw RuntimeException when updating non-existing user")
        void shouldThrowExceptionWhenUserToUpdateNotFound() {
            UserRequestDTO dto = new UserRequestDTO();
            when(repository.findById(1L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.update(1L, dto));

            assertEquals("User not found", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when setting status DESLIGADO with active service order")
        void shouldThrowExceptionWhenDeactivatingUserWithActiveOrder() {
            UserRequestDTO dto = new UserRequestDTO();
            dto.setUserStatus("DESLIGADO");

            when(repository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(serviceOrderRepository.existsByUserIdAndReturnDateIsNull(1L)).thenReturn(true);

            BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                    () -> userService.update(1L, dto));

            assertTrue(exception.getMessage().contains("Não é possível inativar o técnico"));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Should update user successfully when deactivating with no active service order")
        void shouldUpdateUserWhenDeactivatingWithNoActiveOrder() {
            UserRequestDTO dto = new UserRequestDTO();
            dto.setName("Updated John");
            dto.setRegistrationNumber("REG-99999");
            dto.setEmail("updated.john@example.com");
            dto.setPasswordHash("new_password");
            dto.setProfile(Profile.ADMIN);
            dto.setUserStatus("DESLIGADO");
            dto.setCnhTypes(Set.of(CnhType.B));

            when(repository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(serviceOrderRepository.existsByUserIdAndReturnDateIsNull(1L)).thenReturn(false);
            when(repository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

            User result = userService.update(1L, dto);

            assertNotNull(result);
            assertEquals("Updated John", result.getName());
            assertEquals(UserStatus.DESLIGADO, result.getUserStatus());
            verify(repository, times(1)).save(mockUser);
        }

        @Test
        @DisplayName("Should update user successfully with non-DESLIGADO status without checking service order")
        void shouldUpdateUserWithOtherStatusWithoutOrderCheck() {
            UserRequestDTO dto = new UserRequestDTO();
            dto.setName("John Status Change");
            dto.setUserStatus("DISPONIVEL");

            when(repository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(repository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

            User result = userService.update(1L, dto);

            assertNotNull(result);
            assertEquals(UserStatus.DISPONIVEL, result.getUserStatus());
            verify(serviceOrderRepository, never()).existsByUserIdAndReturnDateIsNull(anyLong());
            verify(repository, times(1)).save(mockUser);
        }

        @Test
        @DisplayName("Should update user successfully without modifying status when userStatus is null")
        void shouldUpdateUserWithoutModifyingStatusWhenStatusIsNull() {
            UserRequestDTO dto = new UserRequestDTO();
            dto.setName("John Null Status");
            dto.setUserStatus(null); // Null status

            when(repository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(repository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

            User result = userService.update(1L, dto);

            assertNotNull(result);
            assertEquals("John Null Status", result.getName());
            assertEquals(UserStatus.DISPONIVEL, result.getUserStatus()); // Remains unchanged
            verify(serviceOrderRepository, never()).existsByUserIdAndReturnDateIsNull(anyLong());
            verify(repository, times(1)).save(mockUser);
        }
    }
}