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
    String roleName;
    String title;
    String description;
    String permission;
}