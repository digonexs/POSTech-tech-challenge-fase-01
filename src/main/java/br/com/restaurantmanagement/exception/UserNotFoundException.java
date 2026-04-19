package br.com.restaurantmanagement.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID id) {
        super("Usuário não encontrado com id: " + id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
