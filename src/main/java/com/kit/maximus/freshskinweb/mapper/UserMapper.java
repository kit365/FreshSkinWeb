package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "skinType", ignore = true)
    UserEntity toUserEntity(CreateUserRequest userRequestDTO);

    @Mapping(target = "orders", source = "orders")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "productComparisonId", ignore = true)
    UserResponseDTO toUserResponseDTO(UserEntity userEntity);


    @Mapping(target = "role", ignore = true)
    @Mapping(target = "productComparisonId", ignore = true)
    List<UserResponseDTO> toUserResponseDTO(List<UserEntity> userEntities);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "skinTests", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "skinType", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget UserEntity user,  UpdateUserRequest userDTO);

}
