package com.kit.maximus.freshskinweb.dto.request.productcategory;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class ParentCategoryDTO implements Serializable {
    Long id;
    List<ChildCategoryDTO> children;
}
