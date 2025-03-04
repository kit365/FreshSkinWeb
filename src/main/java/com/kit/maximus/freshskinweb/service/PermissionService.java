package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.permission.PermissionRequest;
import com.kit.maximus.freshskinweb.dto.response.PermissionResponse;
import com.kit.maximus.freshskinweb.entity.PermissionEntity;
import com.kit.maximus.freshskinweb.mapper.PermissionMapper;
import com.kit.maximus.freshskinweb.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class PermissionService {
    PermissionRepository permissionRepository;

    PermissionMapper permissionMapper;

    public boolean createPermission(PermissionRequest permissionRequest) {
        var permissionEntity = permissionMapper.toPermissionEntity(permissionRequest);
        permissionRepository.save(permissionEntity);
        return true;
    }
    public List<PermissionResponse> getAllPermissions() {
        var permissionEntities = permissionRepository.findAll();
        return permissionEntities.stream().map(permissionMapper::toPermissionResponse).collect(Collectors.toList());
    }

    public void deletePermission(String name) {

        permissionRepository.deleteByName(name);
    }
}
