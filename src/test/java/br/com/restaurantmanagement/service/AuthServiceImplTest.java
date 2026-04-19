package br.com.restaurantmanagement.service;

import br.com.restaurantmanagement.domain.model.Client;
import br.com.restaurantmanagement.dto.request.LoginRequest;
import br.com.restaurantmanagement.dto.response.LoginResponse;
import br.com.restaurantmanagement.exception.InvalidCredentialsException;
import br.com.restaurantmanagement.repository.UserRepository;
import br.com.restaurantmanagement.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Deve autenticar usuário e retornar token JWT")
    void shouldAuthenticateAndReturnToken() {
        Client user = new Client();
        user.setId(java.util.UUID.randomUUID());
        user.setLogin("joao.silva");
        user.setPassword("hashedPassword");
        user.setUserType(br.com.restaurantmanagement.domain.model.enums.UserType.CLIENT);

        when(userRepository.findByLogin("joao.silva")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(any(UserDetails.class), anyMap())).thenReturn("mocked.jwt.token");

        LoginRequest request = new LoginRequest("joao.silva", "senha123");
        LoginResponse response = authService.login(request);

        assertThat(response.login()).isEqualTo("joao.silva");
        assertThat(response.token()).isEqualTo("mocked.jwt.token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.message()).isEqualTo("Autenticação realizada com sucesso");
    }

    @Test
    @DisplayName("Deve lançar exceção quando login não existe")
    void shouldThrowExceptionWhenLoginNotFound() {
        when(userRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("inexistente", "senha123")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Login ou senha inválidos");
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha é incorreta")
    void shouldThrowExceptionWhenPasswordIsWrong() {
        Client user = new Client();
        user.setLogin("joao.silva");
        user.setPassword("hashedPassword");

        when(userRepository.findByLogin("joao.silva")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senhaErrada", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("joao.silva", "senhaErrada")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
