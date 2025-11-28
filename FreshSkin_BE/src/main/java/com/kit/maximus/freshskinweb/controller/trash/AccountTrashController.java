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

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("admin/account/trash")
public class AccountTrashController {

    UserService userService;

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getTrash(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortKey,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {

        log.info("GET TRASH ACCOUNTS");
        log.info("Page: {}, Size: {}", page, size);
        log.info("Sort: {} {}", sortKey, sortDirection);
        log.info("Filters - Status: {}, Keyword: {}", status, keyword);

        try {
            Map<String, Object> result = userService.getTrashAccount(
                    page,
                    size,
                    sortKey,
                    sortDirection,
                    status,
                    keyword
            );

            if (result.get("accounts") instanceof List && ((List<?>) result.get("accounts")).isEmpty()) {
                return ResponseAPI.<Map<String, Object>>builder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .message("Không tìm thấy tài khoản quản trị đã xóa phù hợp.")
                        .data(result)
                        .build();
            }

            return ResponseAPI.<Map<String, Object>>builder()
                    .code(HttpStatus.OK.value())
                    .message("Lấy danh sách tài khoản quản trị đã xóa thành công.")
                    .data(result)
                    .build();

        } catch (AppException e) {
            log.error("Error getting trash accounts: {}", e.getMessage());
            return ResponseAPI.<Map<String, Object>>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error getting trash accounts", e);
            return ResponseAPI.<Map<String, Object>>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Có lỗi xảy ra khi lấy danh sách tài khoản quản trị đã xóa.")
                    .build();
        }
    }

    @PatchMapping("restore/{id}")
    public ResponseAPI<UserResponseDTO> restoreAccount(@PathVariable("id") Long id) {
        String message = "Phục hồi tài khoản thành công";
        userService.restore(id);
        log.info(message);
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }

}
