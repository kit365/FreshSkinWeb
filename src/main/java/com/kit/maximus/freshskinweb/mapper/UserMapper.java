package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "role", ignore = true)
    UserEntity toUserEntity(CreateUserRequest userRequestDTO);

    @Mapping(target = "orders", source = "orders")
    UserResponseDTO toUserResponseDTO(UserEntity userEntity);

    UserEntity toUserResponse(UserEntity userEntity);

    List<UserResponseDTO> toUserResponseDTO(List<UserEntity> userEntities);

//    @Mapping(target = "username", ignore = true)
//    @Mapping(target = "password", ignore = true)
//    void updateUser(@MappingTarget UserEntity user,  UserRequestDTO userDTO);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUser(@MappingTarget UserEntity user,  UpdateUserRequest userDTO);




}
