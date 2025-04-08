package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.skin_type_score_range.CreationSkinTypeScoreRangeRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_type_score_range.UpdationSkinTypeScoreRangeRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinTypeScoreRangeResponse;
import com.kit.maximus.freshskinweb.entity.SkinTypeScoreRangeEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SkinTypeScoreRangeMapper {

    @Mapping(target = "skinType", ignore = true)
    SkinTypeScoreRangeEntity toSkinTypeScoreRangeEntity(CreationSkinTypeScoreRangeRequest request);

    SkinTypeScoreRangeResponse toSkinTypeScoreRangeResponse(SkinTypeScoreRangeEntity entity);

    @Mapping(target = "skinType", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update (@MappingTarget SkinTypeScoreRangeEntity entity, UpdationSkinTypeScoreRangeRequest request );
}
