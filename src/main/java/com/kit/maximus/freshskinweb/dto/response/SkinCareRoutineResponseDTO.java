package com.kit.maximus.freshskinweb.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@Builder
public class SkinCareRoutineResponseDTO {
    private SkinCareRountineResponse skinCareRoutine;
    private Page<ProductRoutineDTO> products;
}