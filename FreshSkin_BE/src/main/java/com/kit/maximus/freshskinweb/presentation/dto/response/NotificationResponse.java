package com.kit.maximus.freshskinweb.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    Long id;


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
