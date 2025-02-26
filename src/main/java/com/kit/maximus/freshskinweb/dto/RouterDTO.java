package com.kit.maximus.freshskinweb.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RouterDTO implements Serializable {
    String name;
    String url;

    public RouterDTO(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
