package com.kit.maximus.freshskinweb.dto.request.skin_type;

import lombok.*;
import lombok.experimental.FieldDefaults;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateSkinTypeRequest {
    String type;
    String description;
}
