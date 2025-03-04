package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.permission.PermissionRequest;
import com.kit.maximus.freshskinweb.dto.response.PermissionResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.entity.PermissionEntity;
import com.kit.maximus.freshskinweb.service.PermissionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/permission")
public class PermissionController {

    PermissionService permissionService;

    @PostMapping("/create")
    public ResponseAPI<PermissionResponse> createPermission(@Valid @RequestBody PermissionRequest request) {
        String message = "Tạo quyền thành công";
        permissionService.createPermission(request);
        return ResponseAPI.<PermissionResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @GetMapping("/show")
    public ResponseAPI<List<PermissionResponse>> showAll() {
        String message = "Hiển thị danh sách quyền thành công";
        List<PermissionResponse> data = permissionService.getAllPermissions();
        return ResponseAPI.<List<PermissionResponse>>builder().code(HttpStatus.OK.value()).message(message).data(data).build();
    }

    @DeleteMapping("/delete")
    public ResponseAPI<PermissionResponse> deletePermission(@RequestBody String name) {
        String message = "Xóa quyền thành công";
        permissionService.deletePermission(name);
        return ResponseAPI.<PermissionResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

}
