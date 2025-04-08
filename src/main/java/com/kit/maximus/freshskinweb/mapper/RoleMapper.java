package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.role.CreateRoleRequest;
import com.kit.maximus.freshskinweb.dto.request.role.UpdateRoleRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.RoleResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.RoleEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")

public interface RoleMapper {
    @Mapping(target = "permission", ignore = true)
    RoleEntity toRoleEntity(CreateRoleRequest role);

    @Mapping(target = "title", ignore = true)
    @Mapping(target = "description", ignore = true)
    RoleEntity toRole(CreateRoleRequest role);

    RoleResponseDTO toRoleResponseDTO(RoleEntity role);

    List<RoleResponseDTO> toRoleResponseDTO (List<RoleEntity> RoleEntities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "permission", ignore = true)
    void updateRole(@MappingTarget RoleEntity user, UpdateRoleRequest userDTO);
}
