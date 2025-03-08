package com.kit.maximus.freshskinweb.dto.request.search_keyword;

import jakarta.persistence.Entity;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KeywordRequest {
    String keyword;
    Long count;
}
