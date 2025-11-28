package com.kit.maximus.freshskinweb.dto.request.productcomparison;

import lombok.*;
import lombok.experimental.FieldDefaults;
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class ProductComparisonDTO {
    Long id;
    Long productId;
    Long userID;
}
