package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseAPI <T>{
    long code = 1000;
    String message;
    T data;


    public ResponseAPI(long code, String message, T data)  {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public ResponseAPI(long code, String message) {
        this.code = code;
        this.message = message;
    }
}
