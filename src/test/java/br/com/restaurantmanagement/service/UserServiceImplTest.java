package br.com.restaurantmanagement.service;

import br.com.restaurantmanagement.domain.model.Client;
import br.com.restaurantmanagement.domain.model.RestaurantOwner;
import br.com.restaurantmanagement.domain.model.User;
import br.com.restaurantmanagement.domain.model.enums.UserType;
import br.com.restaurantmanagement.dto.request.AddressRequest;
import br.com.restaurantmanagement.dto.request.CreateUserRequest;
import br.com.restaurantmanagement.dto.request.UpdatePasswordRequest;
import br.com.restaurantmanagement.dto.request.UpdateUserRequest;
import br.com.restaurantmanagement.dto.response.UserResponse;
import br.com.restaurantmanagement.exception.EmailAlreadyExistsException;
import br.com.restaurantmanagement.exception.PasswordMismatchException;
import br.com.restaurantmanagement.exception.UserNotFoundException;
import br.com.restaurantmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private AddressRequest addressRequest;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        addressRequest = new AddressRequest("Rua das Flores", "123", "São Paulo", "01310-100");
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso")
    void shouldCreateClientSuccessfully() {
        CreateUserRequest request = new CreateUserRequest(
                "João Silva", "joao@email.com", "joao.silva", "senha123",
                UserType.CLIENT, addressRequest
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = userService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("João Silva");
        assertThat(response.email()).isEqualTo("joao@email.com");
        verify(userRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("Deve criar um dono de restaurante com sucesso")
    void shouldCreateRestaurantOwnerSuccessfully() {
        CreateUserRequest request = new CreateUserRequest(
                "Maria Restaurante", "maria@restaurante.com", "maria.rest", "senha123",
                UserType.RESTAURANT_OWNER, addressRequest
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = userService.create(request);

        assertThat(response.name()).isEqualTo("Maria Restaurante");
        verify(userRepository).save(any(RestaurantOwner.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com e-mail duplicado")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest(
                "João Silva", "joao@email.com", "joao.silva", "senha123",
                UserType.CLIENT, addressRequest
        );

        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("joao@email.com");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente por ID")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Deve buscar usuários por nome")
    void shouldFindUsersByName() {
        Client client = new Client();
        client.setName("João Silva");
        client.setEmail("joao@email.com");
        client.setLogin("joao.silva");
        client.setPassword("hashedPassword");

        when(userRepository.findByNameContainingIgnoreCase("João")).thenReturn(List.of(client));

        List<UserResponse> responses = userService.findByName("João");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve trocar senha com sucesso")
    void shouldUpdatePasswordSuccessfully() {
        Client user = new Client();
        user.setPassword("hashedOldPassword");

        UpdatePasswordRequest request = new UpdatePasswordRequest(
                "oldPassword", "newPassword123", "newPassword123"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "hashedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("hashedNewPassword");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.updatePassword(userId, request);

        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo("hashedNewPassword");
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha atual for incorreta")
    void shouldThrowExceptionWhenCurrentPasswordIsWrong() {
        Client user = new Client();
        user.setPassword("hashedOldPassword");

        UpdatePasswordRequest request = new UpdatePasswordRequest(
                "wrongPassword", "newPassword123", "newPassword123"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedOldPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(PasswordMismatchException.class)
                .hasMessageContaining("Senha atual incorreta");
    }

    @Test
    @DisplayName("Deve lançar exceção quando confirmação de senha não confere")
    void shouldThrowExceptionWhenPasswordConfirmationMismatch() {
        Client user = new Client();
        user.setPassword("hashedOldPassword");

        UpdatePasswordRequest request = new UpdatePasswordRequest(
                "oldPassword", "newPassword123", "differentPassword"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "hashedOldPassword")).thenReturn(true);

        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(PasswordMismatchException.class)
                .hasMessageContaining("não conferem");
    }

    @Test
    @DisplayName("Deve excluir usuário com sucesso")
    void shouldDeleteUserSuccessfully() {
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.delete(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir usuário inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com e-mail já usado por outro usuário")
    void shouldThrowExceptionWhenUpdatingWithDuplicateEmail() {
        Client user = new Client();
        user.setName("João");
        user.setEmail("joao@email.com");
        user.setLogin("joao");
        user.setPassword("hash");

        UpdateUserRequest request = new UpdateUserRequest(
                "João Atualizado", "outro@email.com", "joao.novo", addressRequest
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("outro@email.com", userId)).thenReturn(true);

        assertThatThrownBy(() -> userService.update(userId, request))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }
}
