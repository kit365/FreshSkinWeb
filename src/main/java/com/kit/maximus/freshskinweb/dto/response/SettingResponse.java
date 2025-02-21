package com.kit.maximus.freshskinweb.dto.response;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class SettingResponse implements Serializable {
    Long settingId;
    String websiteName;
    String logo;
    String phone;
    String email;
    String address;
    String copyright;
    String facebook;
    String twitter;
    String youtube;
    String instagram;
}
