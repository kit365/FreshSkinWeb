package com.kit.maximus.freshskinweb.dto.request.permission;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class PermissionRequest implements Serializable {

    String name;

    String description;
}
