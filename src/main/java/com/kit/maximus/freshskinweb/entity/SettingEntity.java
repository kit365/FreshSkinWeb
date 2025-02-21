package com.kit.maximus.freshskinweb.entity;


import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Setting")
public class SettingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SettingID")
    private Long settingId;

    @Column(name = "WebsiteName")
    private String websiteName;

    @Column(name = "Logo")
    private String logo;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Email")
    private String email;

    @Column(name = "Address")
    private String address;

    @Column(name = "Copyright")
    private String copyright;

    @Column(name = "Facebook")
    private String facebook;

    @Column(name = "Twitter")
    private String twitter;

    @Column(name = "Youtube")
    private String youtube;

    @Column(name = "Instagram")
    private String instagram;
}

