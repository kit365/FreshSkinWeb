package com.kit.maximus.freshskinweb.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogUpdateRequest;
import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdatePasswordWithTokenRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserPasswordRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.users.UserService;
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

//@CrossOrigin(origins = "*")
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
            @RequestPart(value = "avatar", required = false) List<MultipartFile> image) { // Nhận hình ảnh
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

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortKey,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {

        log.info("GET ALL USERS");
        log.info("Page: {}, Size: {}", page, size);
        log.info("Sort: {} {}", sortKey, sortDirection);
        log.info("Filters - Status: {}, Type: {}, Keyword: {}", status, type, keyword);

        try {
            Map<String, Object> result = userService.getAllUser(
                    page,
                    size,
                    sortKey,
                    sortDirection,
                    status,
                    type,
                    keyword
            );

            if (result.get("users") instanceof List && ((List<?>) result.get("users")).isEmpty()) {
                return ResponseAPI.<Map<String, Object>>builder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .message("Không tìm thấy người dùng phù hợp.")
                        .data(result)
                        .build();
            }

            return ResponseAPI.<Map<String, Object>>builder()
                    .code(HttpStatus.OK.value())
                    .message("Lấy danh sách người dùng thành công.")
                    .data(result)
                    .build();

        } catch (AppException e) {
            log.error("Error getting users: {}", e.getMessage());
            return ResponseAPI.<Map<String, Object>>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error getting users", e);
            return ResponseAPI.<Map<String, Object>>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Có lỗi xảy ra khi lấy danh sách người dùng.")
                    .build();
        }
    }

    @GetMapping("/{id}")
    public ResponseAPI<UserResponseDTO> showDetailUser(@PathVariable Long id) {
        String message = "Lấy dữ liệu thành công";
        var result = userService.showDetail(id);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @PatchMapping("change-password/{id}")
    public ResponseAPI<Boolean> updateUserPassword(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserPasswordRequest request) {
        String message = "Cập nhật mật khẩu thành công";
        userService.updateUserPassword(id, request);
        return ResponseAPI.<Boolean>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @PatchMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<UserResponseDTO> editUser(
            @PathVariable("id") Long id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "newImg", required = false) List<MultipartFile> newImg) {

        log.info("requestJson:{}", requestJson);
        log.info("images:{}", newImg);
        String message_succed = "Cập nhập người dùng thành công";
        String message_failed = "Cập nhập người dùng thất bại";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateUserRequest userRequest = objectMapper.readValue(requestJson, UpdateUserRequest.class);
            if (newImg != null) {
                userRequest.setNewImg(newImg);
            }

            UserResponseDTO result = userService.updateUser(id, userRequest);

            log.info("UPDATE USER REQUEST SUCCESS");
            return ResponseAPI.<UserResponseDTO>builder()
                    .code(HttpStatus.OK.value())
                    .data(result)
                    .message(message_succed)
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("UPDATE USER ERROR: " + e.getMessage());
            return ResponseAPI.<UserResponseDTO>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message_failed)
                    .build();
        }
    }

    @PatchMapping("update-password-by-token")
    public ResponseAPI<Boolean> updateAccountPasswordWithToken(@RequestBody @Valid UpdatePasswordWithTokenRequest request) {
        String message = "Cập nhật mật khẩu thành công";
        try {
            boolean result = userService.updateAccountPasswordWithToken(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
            return ResponseAPI.<Boolean>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
        } catch (AppException e) {
            return ResponseAPI.<Boolean>builder().code(HttpStatus.BAD_REQUEST.value()).message(e.getMessage()).build();
        } catch (Exception e) {
            return ResponseAPI.<Boolean>builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Có lỗi xảy ra khi cập nhật mật khẩu.").build();
        }
    }

    @PatchMapping("change-multi")
    public ResponseAPI<String> updateUser(@RequestBody Map<String, Object> request) {

        if (!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            //sua lai thong bao loi
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");
        String status = request.get("status").toString();

        var result = userService.updateMulti(ids, status);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @DeleteMapping("delete")
    public ResponseAPI<String> deleteSelectedUser(@RequestBody Map<String, Object> request) {

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
            String message = "Xóa vĩnh viễn tài khoản thành công";
            userService.delete(id);
            log.info(message);
            return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
        }
    }

    @DeleteMapping("deleteAll")
    public ResponseAPI<UserResponseDTO> deleteUser() {
        {
            String message = "Tất cả tài khoản đã bị xóa";
            userService.deleteAllUsers();
            log.info(message);
            return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
        }
    }

    @PatchMapping("deleteT/{id}")
    public ResponseAPI<UserResponseDTO> deleteUserT(@PathVariable("id") Long id) {
        String message = "Xóa tài khoản thành công";
        userService.deleteTemporarily(id);
        log.info(message);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @PatchMapping("restore/{id}")
    public ResponseAPI<UserResponseDTO> restoreUser(@PathVariable("id") Long id) {
        String message = "Phục hồi tài khoản thành công";
        userService.restore(id);
        log.info(message);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @DeleteMapping("/deleteO/{useId}/{orderId}")
    public ResponseAPI<UserResponseDTO> deleteOrder(@PathVariable Long useId, @PathVariable Long orderId) {
        String message = "Delete order successfully";
        userService.deleteOrder(useId, orderId);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    // CẬP NHẬT RIÊNG STATUS
    @PatchMapping("update/{id}")
    public ResponseAPI<String> updateUserStatus(@PathVariable("id") int id,
                                          @RequestBody Map<String, Object> request) {

        String statusEdit = (String) request.get("statusEdit");
        String status = (String) request.get("status");

        String result = userService.update(id, status, statusEdit);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }
}
