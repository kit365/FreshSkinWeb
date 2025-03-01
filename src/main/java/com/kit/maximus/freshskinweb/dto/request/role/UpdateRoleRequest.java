package com.kit.maximus.freshskinweb.dto.request.role;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class UpdateRoleRequest {
    Long roleId;
    String title;
    String description;
    String permission;
}