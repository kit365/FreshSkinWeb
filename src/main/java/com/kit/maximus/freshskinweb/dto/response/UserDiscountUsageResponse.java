package com.kit.maximus.freshskinweb.dto.response;

import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDiscountUsageResponse {

    Long id;
    UserResponseDTO userID;
    DiscountResponse promoCodeID;
    Boolean discountStatus;
    LocalDateTime UsedAt;
    Boolean DiscountStatus;

    Date createdAt;
    Date updatedAt;
    String status;
    boolean deleted;

}
