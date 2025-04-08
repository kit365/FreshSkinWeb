package com.kit.maximus.freshskinweb.service.users;

import com.kit.maximus.freshskinweb.dto.request.role.AddPermissionRequest;
import com.kit.maximus.freshskinweb.dto.request.role.CreateRoleRequest;
import com.kit.maximus.freshskinweb.dto.request.role.UpdateRoleRequest;
import com.kit.maximus.freshskinweb.dto.response.RoleResponseDTO;
import com.kit.maximus.freshskinweb.entity.RoleEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.RoleMapper;
import com.kit.maximus.freshskinweb.repository.RoleRepository;
import com.kit.maximus.freshskinweb.service.BaseService;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoleService implements BaseService<RoleResponseDTO, CreateRoleRequest, UpdateRoleRequest, Long> {

    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @Override
    public boolean add(CreateRoleRequest request) {
        if (roleRepository.existsByTitle(request.getTitle())) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }
        RoleEntity roleEntity = roleMapper.toRoleEntity(request);
        roleRepository.save(roleEntity);
        return true;
    }

    public boolean addPermission(List<AddPermissionRequest> request) {
        List<Long> id = request.stream()
                .map(AddPermissionRequest::getRoleId)
                .collect(Collectors.toList());

        List<RoleEntity> roles = roleRepository.findAllById(id);

        roles.forEach(roleEntity -> {
            request.stream()
                    .filter(roleRequest -> roleRequest.getRoleId().equals(roleEntity.getRoleId()))
                    .findFirst()
                    .ifPresent(roleRequest -> {
                        List<String> newPermissions = new ArrayList<>(roleRequest.getPermission()); // Chỉ lấy quyền mới
                        roleEntity.setPermission(newPermissions); // Ghi đè thay vì cộng dồn
                    });
        });

        roleRepository.saveAll(roles); // Chỉ cập nhật role có trong request
        return true;
    }




    public RoleResponseDTO getPermissionById(Long id) {
        RoleEntity roleEntity = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        RoleResponseDTO response = roleMapper.toRoleResponseDTO(roleEntity);
        return response;
    }


    @Override
    public RoleResponseDTO update(Long id, UpdateRoleRequest request) {
        RoleEntity roleEntity = getRoleEntityById(id);

        log.info("Tìm thấy role: {}", id);

        // Cập nhật thông tin từ request
        roleMapper.updateRole(roleEntity, request);
        log.info("Cập nhật role id: {}", id);
        return roleMapper.toRoleResponseDTO(roleRepository.save(roleEntity));
    }

    @Override
    public String update(List<Long> id, String status) {
        return "";
    }

    private Status getStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: '{}'", status);
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }

    @Override
    public boolean delete(Long id) {
        RoleEntity roleEntity = getRoleEntityById(id);
        if (roleEntity == null) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        if (roleEntity != null) {
            log.info("Delete role id:{}", id);
            roleRepository.delete(roleEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(List<Long> longs) {
        return false;
    }

    @Override
    public boolean deleteTemporarily(Long id) {
        return true;
    }

    @Override
    public boolean restore(Long id) {
        return false;
    }

    public List<RoleResponseDTO> showAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponseDTO).collect(Collectors.toList());
    }

    @Override
    public RoleResponseDTO showDetail(Long aLong) {
        return roleRepository.findById(aLong).map(roleMapper::toRoleResponseDTO).orElse(null);
    }


    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }

    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }


    public RoleEntity getRoleEntityById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }




}
