package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    Long id;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    String username;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    String order;
//
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    Long review;

    String slugProduct;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String message;

    Boolean isRead;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String status;

    String image;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date time;

}
