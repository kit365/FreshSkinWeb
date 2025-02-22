package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.role.CreateRoleRequest;
import com.kit.maximus.freshskinweb.dto.request.role.UpdateRoleRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.RoleResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.RoleEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")

public interface RoleMapper {

    RoleEntity toRoleEntity(CreateRoleRequest role);

    RoleResponseDTO toRoleResponseDTO(RoleEntity role);

    List<RoleResponseDTO> toRoleResponseDTO (List<RoleEntity> RoleEntities);

    void updateRole(@MappingTarget RoleEntity user, UpdateRoleRequest userDTO);
}
