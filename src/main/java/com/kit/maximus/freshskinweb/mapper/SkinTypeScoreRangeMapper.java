package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.skin_type_score_range.CreationSkinTypeScoreRangeRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_type_score_range.UpdationSkinTypeScoreRangeRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinTypeScoreRangeResponse;
import com.kit.maximus.freshskinweb.entity.SkinTypeScoreRangeEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkinTypeScoreRangeMapper {

    @Mapping(target = "skinType", ignore = true)
    SkinTypeScoreRangeEntity toSkinTypeScoreRangeEntity(CreationSkinTypeScoreRangeRequest request);

    @Mapping(target = "skinType.type", ignore = true)
    @Mapping(target = "skinType.description", ignore = true)
    SkinTypeScoreRangeResponse toSkinTypeScoreRangeResponse(SkinTypeScoreRangeEntity entity);

    @Mapping(target = "skinType.type", ignore = true)
    @Mapping(target = "skinType.description", ignore = true)
    List<SkinTypeScoreRangeResponse> toSkinTypeScoreRangeResponse(List<SkinTypeScoreRangeEntity> entity);

    @Mapping(target = "skinType", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update (@MappingTarget SkinTypeScoreRangeEntity entity, UpdationSkinTypeScoreRangeRequest request );
}
