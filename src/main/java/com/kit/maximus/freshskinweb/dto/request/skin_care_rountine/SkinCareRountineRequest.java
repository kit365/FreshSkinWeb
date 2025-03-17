package com.kit.maximus.freshskinweb.dto.request.skin_care_rountine;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkinCareRountineRequest {
    Long skinTypeEntity;
    String rountine;
}
