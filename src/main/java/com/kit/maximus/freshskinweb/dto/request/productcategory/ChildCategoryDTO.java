package com.kit.maximus.freshskinweb.dto.request.productcategory;

import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@ToString
public class ChildCategoryDTO implements Serializable {
    String title;
    String slug;
    String description;
    Integer position;
    boolean featured;
    String image;
    String status;

}
