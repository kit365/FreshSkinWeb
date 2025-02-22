package com.kit.maximus.freshskinweb.dto.request.skin_type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateSkinTypeRequest {
    String type;
    String description;
}
