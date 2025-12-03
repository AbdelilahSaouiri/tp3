package net.ensah.tp3.user.service;

import net.ensah.tp3.user.dto.UserRequest;
import net.ensah.tp3.user.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);
    List<UserResponse> getAll();
    UserResponse getById(Long id);
    UserResponse update(Long id, UserRequest request);
    void delete(Long id);
}
