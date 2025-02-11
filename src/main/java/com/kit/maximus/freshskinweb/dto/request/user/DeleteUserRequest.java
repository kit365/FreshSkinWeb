package com.kit.maximus.freshskinweb.dto.request.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class DeleteUserRequest implements Serializable {
    Long id;
}
