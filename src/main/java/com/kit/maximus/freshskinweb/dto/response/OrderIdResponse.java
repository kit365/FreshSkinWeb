package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderIdResponse implements Serializable {
    Long orderId;
}




