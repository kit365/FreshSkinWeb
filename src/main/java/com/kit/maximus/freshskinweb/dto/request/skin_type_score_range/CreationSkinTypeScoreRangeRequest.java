package com.kit.maximus.freshskinweb.dto.request.skin_type_score_range;

import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreationSkinTypeScoreRangeRequest {
    Double minScore;

    Double maxScore;

    Long skinType;
}
