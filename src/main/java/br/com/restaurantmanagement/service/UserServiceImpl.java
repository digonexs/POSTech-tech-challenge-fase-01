package br.com.restaurantmanagement.service;

import br.com.restaurantmanagement.domain.model.Address;
import br.com.restaurantmanagement.domain.model.Client;
import br.com.restaurantmanagement.domain.model.RestaurantOwner;
import br.com.restaurantmanagement.domain.model.User;
import br.com.restaurantmanagement.domain.model.enums.UserType;
import br.com.restaurantmanagement.dto.request.AddressRequest;
import br.com.restaurantmanagement.dto.request.CreateUserRequest;
import br.com.restaurantmanagement.dto.request.UpdatePasswordRequest;
import br.com.restaurantmanagement.dto.request.UpdateUserRequest;
import br.com.restaurantmanagement.dto.response.AddressResponse;
import br.com.restaurantmanagement.dto.response.UserResponse;
import br.com.restaurantmanagement.exception.EmailAlreadyExistsException;
import br.com.restaurantmanagement.exception.PasswordMismatchException;
import br.com.restaurantmanagement.exception.UserNotFoundException;
import br.com.restaurantmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = instantiateUser(request.userType());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setLogin(request.login());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAddress(toAddress(request.address()));
        // Necessário pois userType é insertable=false — Hibernate não popula
        // o campo Java no objeto em memória após o INSERT, apenas na leitura.
        user.setUserType(request.userType());

        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return toResponse(findUserById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = findUserById(id);

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new EmailAlreadyExistsException(request.email());
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setLogin(request.login());
        user.setAddress(toAddress(request.address()));

        return toResponse(userRepository.save(user));
    }

    @Override
    public void updatePassword(UUID id, UpdatePasswordRequest request) {
        User user = findUserById(id);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new PasswordMismatchException("Senha atual incorreta");
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new PasswordMismatchException("Nova senha e confirmação não conferem");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private User instantiateUser(UserType userType) {
        return switch (userType) {
            case RESTAURANT_OWNER -> new RestaurantOwner();
            case CLIENT -> new Client();
        };
    }

    private Address toAddress(AddressRequest req) {
        return Address.builder()
                .street(req.street())
                .number(req.number())
                .city(req.city())
                .zipCode(req.zipCode())
                .build();
    }

    private UserResponse toResponse(User user) {
        AddressResponse addressResponse = null;
        if (user.getAddress() != null) {
            addressResponse = new AddressResponse(
                    user.getAddress().getStreet(),
                    user.getAddress().getNumber(),
                    user.getAddress().getCity(),
                    user.getAddress().getZipCode()
            );
        }
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getUserType(),
                user.getLastModifiedDate(),
                addressResponse
        );
    }
}
