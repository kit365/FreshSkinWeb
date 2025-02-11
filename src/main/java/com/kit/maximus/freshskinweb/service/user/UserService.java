package com.kit.maximus.freshskinweb.service.user;


import com.kit.maximus.freshskinweb.dto.request.user.UserRequestDTO;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserResponseDTO add(CreateUserRequest request);
    boolean delete(Long id);
    boolean delete(List<Long> id);
    boolean  deleteTemporarily(Long id);
    boolean  deleteTemporarily(List<Long> id);

    boolean restore(Long id);
    boolean restore(List<Long> id);

    UserResponseDTO update(Long id, UpdateUserRequest request);
    List<UserResponseDTO> update(List<UserRequestDTO> listRequest);
    Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection);
}