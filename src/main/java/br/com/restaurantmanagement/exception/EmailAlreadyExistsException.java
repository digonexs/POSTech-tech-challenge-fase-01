package br.com.restaurantmanagement.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("E-mail já cadastrado: " + email);
    }
}
