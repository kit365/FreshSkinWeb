package com.kit.maximus.freshskinweb.dto.request.setting;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class CreateSettingRequest {
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
