package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.permission.PermissionRequest;
import com.kit.maximus.freshskinweb.dto.response.PermissionResponse;
import com.kit.maximus.freshskinweb.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionEntity toPermissionEntity(PermissionRequest permissionEntity);

    PermissionResponse toPermissionResponse(PermissionEntity permissionEntity);

}
