package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.role.CreateRoleRequest;
import com.kit.maximus.freshskinweb.dto.request.role.UpdateRoleRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.RoleResponseDTO;
import com.kit.maximus.freshskinweb.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@Slf4j
@RequestMapping("admin/role")
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


    @PatchMapping("update/{id}")
    public ResponseAPI<RoleResponseDTO> updateRole(@PathVariable("id") Long id, @Valid @RequestBody UpdateRoleRequest roleRequestDTO){
        String message = "Update role successfully";
        var result = roleService.update(id, roleRequestDTO);
        return ResponseAPI.<RoleResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<RoleResponseDTO> deleteRole(@PathVariable("id") Long id){
        String message = "Delete role successfully";
        roleService.delete(id);
        log.info(message);
        return ResponseAPI.<RoleResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }


    @PatchMapping("deleteT/{id}")
    public ResponseAPI<RoleResponseDTO> deleteRoleT(@PathVariable("id") Long id){
        String message = "Delete user successfully";
        roleService.deleteTemporarily(id);
        log.info(message);
        return ResponseAPI.<RoleResponseDTO>builder().code(HttpStatus.OK.value()).message(message).build();
    }

}
