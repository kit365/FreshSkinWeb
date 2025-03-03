package com.kit.maximus.freshskinweb.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.request.product.CreateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@Slf4j
@RequestMapping("admin/users")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAdminController {

    UserService userService;

    @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<UserResponseDTO> addUser(
            @RequestPart("request") String requestJson,  // Nhận JSON dưới dạng String
            @RequestPart(value = "avatar", required = false) MultipartFile image) { // Nhận hình ảnh
        log.info("requestJson:{}", requestJson);
        log.info("images:{}", image);
        String message_succed = "Tạo user thành công";
        String message_failed = "Tạo user thất bại";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CreateUserRequest createProductRequest = objectMapper.readValue(requestJson, CreateUserRequest.class);
            createProductRequest.setAvatar(image);

            userService.add(createProductRequest);

            log.info("CREATE USER REQUEST SUCCESS");

            return ResponseAPI.<UserResponseDTO>builder()
                    .code(HttpStatus.OK.value())
                    .message(message_succed)
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("CREATE USER ERROR: " + e.getMessage());

            return ResponseAPI.<UserResponseDTO>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message_failed)
                    .build();
        }
    }

    @PostMapping("addO/{id}")
    public ResponseAPI<UserResponseDTO> addOrder(@PathVariable("id") Long id, @RequestBody OrderRequest requestDTO) {
        String message = "Create user successfully";
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(userService.addOrder(id, requestDTO)).build();
    }

    @GetMapping("show")
    public ResponseAPI<List<UserResponseDTO>> getUsers() {
        String message = "Get all users successfully";
        var result = userService.getAllUsers();
        return ResponseAPI.<List<UserResponseDTO>>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping("show/{id}")
    public ResponseAPI<UserResponseDTO> showDetailUser(@PathVariable Long id) {
        String message = "Get user successfully";
        var result = userService.showDetail(id);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping("search")
    public ResponseAPI<List<UserResponseDTO>> searchUser(@RequestParam("keyword") String name) {
        String message = "Search user successfully";
        var user = userService.getUserByUsername(name);
//        return Collections.singletonList(userService.getUserByUsername(name));
        return ResponseAPI.<List<UserResponseDTO>>builder().code(HttpStatus.OK.value()).message(message).data(user).build();
    }

    @PatchMapping("edit/{id}")
    public ResponseAPI<UserResponseDTO> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest userRequestDTO) {
        String message = "Update user successfully";
        var result = userService.update(id, userRequestDTO);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }


    @PatchMapping("change-multi")
    public ResponseAPI<String> updataUser(@RequestBody Map<String, Object> request) {

        if (!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            //sua lai thong bao loi
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");
        String status = request.get("status").toString();

        var result = userService.update(ids, status);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @DeleteMapping("delete")
    public ResponseAPI<String> deleteBlogCategory(@RequestBody Map<String, Object> request) {

        if (!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");

        String message_succed = "delete User successfull";
        String message_failed = "delete User failed";
        var result = userService.delete(ids);
        if (result) {
            log.info("BlogCategory delete successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("BlogCategory delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }


    @DeleteMapping("delete/{id}")
    public ResponseAPI<UserResponseDTO> deleteUser(@PathVariable("id") Long id) {
        {
            String message = "Delete user successfully";
            userService.delete(id);
            log.info(message);
            return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
        }
    }

    @DeleteMapping("deleteAll")
    public ResponseAPI<UserResponseDTO> deleteUser() {
        {
            String message = "Delete all users successfully";
            userService.deleteAllUsers();
            log.info(message);
            return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
        }
    }

    @PatchMapping("deleteT/{id}")
    public ResponseAPI<UserResponseDTO> deleteUserT(@PathVariable("id") Long id) {
        String message = "Delete user successfully";
        userService.deleteTemporarily(id);
        log.info(message);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @DeleteMapping("/deleteO/{useId}/{orderId}")
    public ResponseAPI<UserResponseDTO> deleteOrder(@PathVariable Long useId, @PathVariable Long orderId) {
        String message = "Delete order successfully";
        userService.deleteOrder(useId, orderId);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }
}
