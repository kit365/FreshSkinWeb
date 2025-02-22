package com.kit.maximus.freshskinweb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SkinTypeResponse {
    long id;
    String type;
    String description;
}
