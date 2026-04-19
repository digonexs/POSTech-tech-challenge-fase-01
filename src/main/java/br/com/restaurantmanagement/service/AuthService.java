package br.com.restaurantmanagement.service;

import br.com.restaurantmanagement.dto.request.LoginRequest;
import br.com.restaurantmanagement.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
