package com.kit.maximus.freshskinweb.presentation.dto.request.search_keyword;

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
