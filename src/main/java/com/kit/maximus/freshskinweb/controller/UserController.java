package com.kit.maximus.freshskinweb.controller;

import com.kit.maximus.freshskinweb.dto.request.UserRequestDTO;
import com.kit.maximus.freshskinweb.dto.request.ValidationGroups;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("admin/users")
@RestController
public class UserController {

    final UserService userService;

    @PostMapping("create")
    public ResponseEntity<UserResponseDTO> addUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.add(requestDTO));
    }

    @GetMapping("show")
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("search")
    public ResponseEntity<List<UserResponseDTO>> searchUser(@RequestParam("keyword") String name) {
        return ResponseEntity.ok(Collections.singletonList(userService.getUserByUsername(name)));
    }

    @PatchMapping("update/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserRequestDTO userRequestDTO){
        UserResponseDTO result = userService.update(id, userRequestDTO);
        if (result != null) {
            log.info("User updated successfully");
            return ResponseEntity.ok(result);
        }
        log.info("User update failed");
        return ResponseEntity.badRequest().body("User update failed");
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long id){
        boolean result = userService.delete(id);
        if (result) {
            log.info("User deleted successfully");
            return ResponseEntity.ok("User deleted successfully");
        }
        log.info("User delete failed");
        return ResponseEntity.badRequest().body("User delete failed");
    }

    @PatchMapping("deleteT/{id}")
    public ResponseEntity<String> deleteUserT(@PathVariable("id") Long id){
        boolean result = userService.deleteTemporarily(id);
        if (result) {
            log.info("User deleted successfully");
            return ResponseEntity.ok("User deleted successfully");
        }
        log.info("User delete failed");
        return ResponseEntity.badRequest().body("User delete failed");
    }
}
