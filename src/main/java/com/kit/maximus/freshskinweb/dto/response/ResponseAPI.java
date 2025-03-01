package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseAPI <T>{
    long code = HttpStatus.OK.value();
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
