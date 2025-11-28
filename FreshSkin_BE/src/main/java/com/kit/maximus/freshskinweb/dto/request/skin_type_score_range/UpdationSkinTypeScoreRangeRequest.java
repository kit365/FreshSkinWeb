package com.kit.maximus.freshskinweb.dto.request.skin_type_score_range;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdationSkinTypeScoreRangeRequest {

    Double minScore;

    Double maxScore;
}
