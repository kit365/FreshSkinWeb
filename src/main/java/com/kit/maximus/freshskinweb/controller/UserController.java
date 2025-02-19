package com.kit.maximus.freshskinweb.controller;

import com.kit.maximus.freshskinweb.dto.request.order.CreateOrderRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@Slf4j
@RequestMapping("admin/users")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping("create")
    public ResponseAPI<UserResponseDTO> addUser(@Valid @RequestBody CreateUserRequest requestDTO) {
        String message = "Create user successfully";
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(userService.add(requestDTO)).build();
    }

    @PostMapping("addO/{id}")
    public ResponseAPI<UserResponseDTO> addOrder(@PathVariable("id") Long id, @RequestBody CreateOrderRequest requestDTO) {
        String message = "Create user successfully";
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(userService.addOrder(id, requestDTO)).build();
    }

    @GetMapping("show")
    public ResponseAPI<List<UserResponseDTO>> getUsers() {
        String message = "Get all users successfully";
        var result = userService.getAllUsers();
        return ResponseAPI.<List<UserResponseDTO>>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping("search")
    public ResponseAPI<List<UserResponseDTO>> searchUser(@RequestParam("keyword") String name) {
        String message = "Search user successfully";
        var user = userService.getUserByUsername(name);
//        return Collections.singletonList(userService.getUserByUsername(name));
        return ResponseAPI.<List<UserResponseDTO>>builder().code(HttpStatus.OK.value()).message(message).data(user).build();
    }

    @PatchMapping("update/{id}")
    public ResponseAPI<UserResponseDTO> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest userRequestDTO){
        String message = "Update user successfully";
        var result = userService.update(id, userRequestDTO);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<UserResponseDTO> deleteUser(@PathVariable("id") Long id){{
        String message = "Delete user successfully";
        userService.delete(id);
        log.info(message);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }
    }


    @PatchMapping("deleteT/{id}")
    public ResponseAPI<UserResponseDTO> deleteUserT(@PathVariable("id") Long id){
        String message = "Delete user successfully";
        userService.deleteTemporarily(id);
        log.info(message);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @DeleteMapping("/deleteO/{useId}/{orderId}")
    public ResponseAPI<UserResponseDTO> deleteOrder(@PathVariable Long useId, @PathVariable Long orderId){
        String message = "Delete order successfully";
        userService.deleteOrder(useId, orderId);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }
}
