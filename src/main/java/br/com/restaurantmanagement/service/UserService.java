package br.com.restaurantmanagement.service;

import br.com.restaurantmanagement.dto.request.CreateUserRequest;
import br.com.restaurantmanagement.dto.request.UpdatePasswordRequest;
import br.com.restaurantmanagement.dto.request.UpdateUserRequest;
import br.com.restaurantmanagement.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    UserResponse findById(UUID id);

    List<UserResponse> findByName(String name);

    UserResponse update(UUID id, UpdateUserRequest request);

    void updatePassword(UUID id, UpdatePasswordRequest request);

    void delete(UUID id);
}
