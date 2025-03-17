package com.kit.maximus.freshskinweb.controller.trash;

import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.users.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("admin/users/trash")
public class UserTrashController {

    UserService userService;

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getTrash(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortKey,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {

        log.info("GET TRASH USERS");
        log.info("Page: {}, Size: {}", page, size);
        log.info("Sort: {} {}", sortKey, sortDirection);
        log.info("Filters - Status: {}, Keyword: {}", status, keyword);

        try {
            Map<String, Object> result = userService.getTrash(
                    page,
                    size,
                    sortKey,
                    sortDirection,
                    status,
                    keyword
            );

            if (result.get("users") instanceof List && ((List<?>) result.get("users")).isEmpty()) {
                return ResponseAPI.<Map<String, Object>>builder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .message("Không tìm thấy người dùng đã xóa phù hợp.")
                        .data(result)
                        .build();
            }

            return ResponseAPI.<Map<String, Object>>builder()
                    .code(HttpStatus.OK.value())
                    .message("Lấy danh sách người dùng đã xóa thành công.")
                    .data(result)
                    .build();

        } catch (AppException e) {
            log.error("Error getting trash users: {}", e.getMessage());
            return ResponseAPI.<Map<String, Object>>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error getting trash users", e);
            return ResponseAPI.<Map<String, Object>>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Có lỗi xảy ra khi lấy danh sách người dùng đã xóa.")
                    .build();
        }
    }

    @PatchMapping("restore/{id}")
    public ResponseAPI<UserResponseDTO> restoreUser(@PathVariable("id") Long id) {
        String message = "Phục hồi tài khoản thành công";
        userService.restore(id);
        log.info(message);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
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
}