package br.com.restaurantmanagement.service;

import br.com.restaurantmanagement.domain.model.User;
import br.com.restaurantmanagement.dto.request.LoginRequest;
import br.com.restaurantmanagement.dto.response.LoginResponse;
import br.com.restaurantmanagement.exception.InvalidCredentialsException;
import br.com.restaurantmanagement.repository.UserRepository;
import br.com.restaurantmanagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLogin(request.login())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserType().name()))
        );

        String token = jwtService.generateToken(
                userDetails,
                Map.of("userId", user.getId(), "userType", user.getUserType())
        );

        return new LoginResponse(
                user.getId(),
                user.getLogin(),
                user.getUserType(),
                token,
                "Bearer",
                "Autenticação realizada com sucesso"
        );
    }
}
