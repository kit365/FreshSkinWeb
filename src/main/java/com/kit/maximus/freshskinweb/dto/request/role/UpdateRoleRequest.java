package com.kit.maximus.freshskinweb.dto.request.role;

import com.kit.maximus.freshskinweb.entity.AbstractEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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
    List<String> permission;
    String status;

}