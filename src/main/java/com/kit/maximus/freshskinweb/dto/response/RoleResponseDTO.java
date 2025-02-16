package com.kit.maximus.freshskinweb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class RoleResponseDTO {
    Long id;
    String roleName;
    String title;
    String description;
    String permission;
}
