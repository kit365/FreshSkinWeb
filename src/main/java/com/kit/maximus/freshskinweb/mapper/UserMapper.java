package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.UserRequestDTO;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toUserEntity(UserRequestDTO userRequestDTO);


    UserResponseDTO toUserResponseDTO(UserEntity userEntity);

    @Mapping(target = "username", ignore = true) // Không update username
    @Mapping(target = "id", ignore = true) //không update id
    void updateUser(@MappingTarget UserEntity user,  UserRequestDTO userDTO);



}
