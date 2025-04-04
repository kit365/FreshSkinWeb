package com.kit.maximus.freshskinweb.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogUpdateRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "*")
@Slf4j
@RequestMapping("admin/account")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountAdminController {

    UserService userService;

    @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseAPI<UserResponseDTO> addAccount(
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

    @GetMapping("/post-managers")
    public ResponseAPI<List<UserResponseDTO>> getPostManagers() {
        String message = "Lấy danh sách quản lý bài viết thành công";
        var result = userService.getUsersWithPostManagerRole();
        return ResponseAPI.<List<UserResponseDTO>>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping("{id}")
    public ResponseAPI<UserResponseDTO> showDetailAccount(@PathVariable Long id) {
        String message = "Lấy dữ liệu thành công";
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
        Map<String, Object> result = userService.getAllAccount(status, keyword, pageable);

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
        String message = "Cập nhật mật khẩu thành công";
        userService.updateAccountPassword(id, request);
        return ResponseAPI.<Boolean>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @PatchMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<UserResponseDTO> editAccount(
            @PathVariable("id") Long id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "newImg", required = false) List<MultipartFile> newImg) {

        log.info("requestJson:{}", requestJson);
        log.info("images:{}", newImg);
        String message_succed = "Cập nhập tài khoản quản trị thành công";
        String message_failed = "Cập nhập tài khoản quản trị thất bại";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateUserRequest accountRequest = objectMapper.readValue(requestJson, UpdateUserRequest.class);
            if (newImg != null && !newImg.isEmpty()) {
                // Xử lý và lưu ảnh
                for (MultipartFile image : newImg) {
                    // Lưu ảnh vào một vị trí hoặc xử lý theo yêu cầu
                    // Ví dụ: saveImage(image);
                    log.info("Processing image: {}", image.getOriginalFilename());
                }
                accountRequest.setNewImg(newImg);
            }

            UserResponseDTO result = userService.updateAccount(id, accountRequest);

            log.info("UPDATE ACCOUNT REQUEST SUCCESS");
            return ResponseAPI.<UserResponseDTO>builder()
                    .code(HttpStatus.OK.value())
                    .data(result)
                    .message(message_succed)
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("UPDATE ACCOUNT ERROR: " + e.getMessage());
            return ResponseAPI.<UserResponseDTO>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message_failed)
                    .build();
        }
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
        String message = "Cập nhật trạng thái tài khoản thành công ";
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @DeleteMapping("delete")
    public ResponseAPI<String> deleteBySelectedAccount(@RequestBody Map<String, Object> request) {

        if (!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");

        String message_succed = "Xóa vĩnh viễn tài khoản thành công";
        String message_failed = "Xóa vĩnh viễn tài khoản thất bại";
        var result = userService.deleteSelectedAccount(ids);
        if (result) {
            log.info("Account delete successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Xóa tài khoản không thành công");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<UserResponseDTO> deleteUser(@PathVariable("id") Long id) {
        {
            String message = "Xóa vĩnh viễn tài khoản thành công";
            userService.deleteAccount(id);
            log.info(message);
            return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
        }
    }

    @PatchMapping("deleteT/{id}")
    public ResponseAPI<UserResponseDTO> deleteAccountT(@PathVariable("id") Long id) {
        String message = "Xóa tài khoản thành công";
        userService.deleteTemporarily(id);
        log.info(message);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @PatchMapping("restore/{id}")
    public ResponseAPI<UserResponseDTO> restoreAccount(@PathVariable("id") Long id) {
        String message = "Phục hồi tài khoản thành công";
        userService.restore(id);
        log.info(message);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    // CẬP NHẬT RIÊNG STATUS
    @PatchMapping("update/{id}")
    public ResponseAPI<String> updateAccountStatus(@PathVariable("id") int id,
                                                   @RequestBody Map<String, Object> request) {

        String statusEdit = (String) request.get("statusEdit");
        String status = (String) request.get("status");

        String result = userService.update(id, status, statusEdit);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }
}