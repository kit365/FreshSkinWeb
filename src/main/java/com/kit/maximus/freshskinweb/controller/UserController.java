package com.kit.maximus.freshskinweb.controller;

import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.service.user.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("https://project-swp391-n9j6.onrender.com")
@Slf4j
@RequestMapping("admin/users")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserServiceImpl userServiceImpl;

    @PostMapping("create")
    public ResponseAPI<UserResponseDTO> addUser(@Valid @RequestBody CreateUserRequest requestDTO) {
        String message = "Create user successfully";
        return ResponseAPI.<UserResponseDTO>builder().code(1000).message(message).data(userServiceImpl.add(requestDTO)).build();
    }

    @GetMapping("show")
    public List<UserResponseDTO> getUsers() {
        return userServiceImpl.getAllUsers();
    }

    @GetMapping("search")
    public ResponseAPI<List<UserResponseDTO>> searchUser(@RequestParam("keyword") String name) {
        var user = userServiceImpl.getUserByUsername(name);
//        return Collections.singletonList(userService.getUserByUsername(name));
        return ResponseAPI.<List<UserResponseDTO>>builder().code(1000).data(user).build();
    }

    @PatchMapping("update/{id}")
    public ResponseAPI<UserResponseDTO> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest userRequestDTO){
        String message = "Update user successfully";
        var result = userServiceImpl.update(id, userRequestDTO);
        return ResponseAPI.<UserResponseDTO>builder().code(1000).message(message).data(result).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<UserResponseDTO> deleteUser(@PathVariable("id") Long id){{
        String message = "Delete user successfully";
        userServiceImpl.delete(id);
        log.info(message);
        return ResponseAPI.<UserResponseDTO>builder().code(1000).message(message).build();
    }
    }


    @PatchMapping("deleteT/{id}")
    public ResponseAPI<UserResponseDTO> deleteUserT(@PathVariable("id") Long id){
        String message = "Delete user successfully";
        userServiceImpl.deleteTemporarily(id);
        log.info(message);
        return ResponseAPI.<UserResponseDTO>builder().code(1000).message(message).build();
    }
}
