package com.kit.maximus.freshskinweb.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@Slf4j
@RequestMapping("admin/account")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {

    UserService userService;

    @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseAPI<UserResponseDTO> addAccount(
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

    @GetMapping("{id}")
    public ResponseAPI<UserResponseDTO> showDetailAccount(@PathVariable Long id) {
        String message = "Get account successfully";
        var result = userService.showDetailByRole(id);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping()
    public ResponseAPI<Map<String, Object>> searchUsers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET ALL ACCOUNTS");
        log.info("Received status: {}", status);  // ➤ Log kiểm tra giá trị status
        log.info("Received keyword: {}", keyword);  // ➤ Log kiểm tra giá trị keyword

        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> result = userService.getAll(status, keyword, pageable);

        if (result.get("users") instanceof List && ((List<?>) result.get("users")).isEmpty()) {
            return ResponseAPI.<Map<String, Object>>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Không tìm thấy tài khoản phù hợp với từ khóa đã nhập.")
                    .data(result)
                    .build();
        }

        return ResponseAPI.<Map<String, Object>>builder()
                .code(HttpStatus.OK.value())
                .message("Tìm thấy danh sách tài khoản.")
                .data(result)
                .build();
    }

    @PatchMapping("change-password/{id}")
    public ResponseAPI<Boolean> updateAccountPassword(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest request) {
        String message = "Update account password successfully";
        userService.updatePassword(id, request);
        return ResponseAPI.<Boolean>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @PatchMapping("edit/{id}")
    public ResponseAPI<UserResponseDTO> updateAccount(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest userRequestDTO) {
        String message = "Update account successfully";
        var result = userService.updateAccount(id, userRequestDTO);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @PatchMapping("change-multi")
    public ResponseAPI<String> updataAccount(@RequestBody Map<String, Object> request) {

        if (!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            //sua lai thong bao loi
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");
        String status = request.get("status").toString();

        var result = userService.updateMulti(ids, status);
        String message = "Cập nhật Account thành công ";
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @DeleteMapping("delete")
    public ResponseAPI<String> deleteBySelectedAccount(@RequestBody Map<String, Object> request) {

        if (!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");

        String message_succed = "delete User successfull";
        String message_failed = "delete User failed";
        var result = userService.deleteSelectedAccount(ids);
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
            userService.deleteAccount(id);
            log.info(message);
            return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
        }
    }
}
