package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.role.AddPermissionRequest;
import com.kit.maximus.freshskinweb.dto.request.role.CreateRoleRequest;
import com.kit.maximus.freshskinweb.dto.request.role.UpdateRoleRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.RoleResponseDTO;
import com.kit.maximus.freshskinweb.service.users.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "*")
@Slf4j
@RequestMapping("admin/roles")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @PostMapping("create")
    public ResponseAPI<RoleResponseDTO> addRole(@Valid @RequestBody CreateRoleRequest requestDTO) {
        String message = "Create role successfully";
        var res = roleService.add(requestDTO);
        return ResponseAPI.<RoleResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @PatchMapping("add-permission")
    public ResponseAPI<RoleResponseDTO> addPermission(@RequestBody List<AddPermissionRequest> request) {
        request.forEach(addPermissionRequest -> {
            System.out.println(addPermissionRequest.getRoleId());
            System.out.println(addPermissionRequest.getPermission());
        });
        String message = "Thêm quyền thành công";
        var res = roleService.addPermission(request);
        return ResponseAPI.<RoleResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }

//    @GetMapping("permission/{id}")
//    public ResponseAPI<RoleResponseDTO> getPermission(@PathVariable Long id) {
//        String message = "Hiển thị quyền thành công";
//        var res = roleService.getPermissionById(id);
//        return ResponseAPI.<RoleResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(res).build();
//    }


    @PatchMapping("edit/{id}")
    public ResponseAPI<RoleResponseDTO> updateRole(@PathVariable("id") Long id, @Valid @RequestBody UpdateRoleRequest roleRequestDTO){
        String message = "Update role successfully";
        var result = roleService.update(id, roleRequestDTO);
        return ResponseAPI.<RoleResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping("/{id}")
    public ResponseAPI<RoleResponseDTO> showDetail(@PathVariable("id") Long id){
        String message = "Show role detail successfully";
        var result = roleService.showDetail(id);
        return ResponseAPI.<RoleResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping
    public ResponseAPI<List<RoleResponseDTO>> showAll(){
        var result = roleService.showAll();
        return ResponseAPI.<List<RoleResponseDTO>>builder().data(result).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<RoleResponseDTO> deleteRole(@PathVariable("id") Long id){
        String message = "Delete role successfully";
        roleService.delete(id);
        log.info(message);
        return ResponseAPI.<RoleResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }

}
