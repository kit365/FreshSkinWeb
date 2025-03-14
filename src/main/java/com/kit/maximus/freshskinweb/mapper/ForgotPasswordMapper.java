package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.forgot_password.ForgotPasswordRequest;
import com.kit.maximus.freshskinweb.dto.response.ForgotPasswordResponse;
import com.kit.maximus.freshskinweb.entity.ForgotPasswordEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ForgotPasswordMapper {
    ForgotPasswordEntity toForgotPasswordEntity(ForgotPasswordRequest request);

    ForgotPasswordResponse toForgotPasswordResponse(ForgotPasswordEntity entity);

    void update(@MappingTarget ForgotPasswordEntity forgotPasswordEntity, ForgotPasswordRequest request);
}
