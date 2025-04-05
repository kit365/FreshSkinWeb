package com.kit.maximus.freshskinweb.dto.request.skin_care_rountine;

import com.kit.maximus.freshskinweb.dto.request.rountine_step.CreationRountineStepRequest;
import com.kit.maximus.freshskinweb.entity.RountineStepEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkinCareRountineRequest {
    Long skinType;
    String title;
    String description;
    List<CreationRountineStepRequest> rountineStep;
}
