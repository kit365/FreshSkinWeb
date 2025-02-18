package com.kit.maximus.freshskinweb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class RoleResponseDTO implements Serializable {
    Long id;
    String roleName;
    String title;
    String description;
    String permission;
}
