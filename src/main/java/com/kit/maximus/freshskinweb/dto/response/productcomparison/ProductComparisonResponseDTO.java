package com.kit.maximus.freshskinweb.dto.response.productcomparison;

import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class ProductComparisonResponseDTO {
    Long userID;
    Long id;
    List<ProductResponseDTO> products;
}
