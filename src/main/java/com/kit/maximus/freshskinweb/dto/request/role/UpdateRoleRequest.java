package com.kit.maximus.freshskinweb.dto.request.role;

import com.kit.maximus.freshskinweb.entity.AbstractEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRoleRequest  {
    Long roleId;
    String title;
    String description;
    String permission;
    String status;

}