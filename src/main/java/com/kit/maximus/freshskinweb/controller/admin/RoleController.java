package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.role.CreateRoleRequest;
import com.kit.maximus.freshskinweb.dto.request.role.UpdateRoleRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.RoleResponseDTO;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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


    @PatchMapping("edit/{id}")
    public ResponseAPI<RoleResponseDTO> updateRole(@PathVariable("id") Long id, @Valid @RequestBody UpdateRoleRequest roleRequestDTO){
        String message = "Update role successfully";
        var result = roleService.update(id, roleRequestDTO);
        return ResponseAPI.<RoleResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping("/{id}")
    public ResponseAPI<RoleResponseDTO> showDetail(@PathVariable("id") Long id){
        var result = roleService.showDetail(id);
        return ResponseAPI.<RoleResponseDTO>builder().data(result).build();
    }

    @GetMapping
    public ResponseAPI<List<RoleResponseDTO>> showAll(){
        var result = roleService.showAll();
        return ResponseAPI.<List<RoleResponseDTO>>builder().data(result).build();
    }

    @PatchMapping("change-multi")
    public ResponseAPI<String> updateRole(@RequestBody Map<String, Object> request) {

        if (!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            //sua lai thong bao loi
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");
        String status = request.get("status").toString();

        var result = roleService.update(ids, status);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
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
