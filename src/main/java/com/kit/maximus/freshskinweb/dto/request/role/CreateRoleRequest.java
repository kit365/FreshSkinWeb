package com.kit.maximus.freshskinweb.dto.request.role;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRoleRequest {
    String title;
    String description;
    List<String> permission = new ArrayList<>();
}