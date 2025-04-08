package com.kit.maximus.freshskinweb.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Setting")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SettingID")
    Long settingId;

    @Column(name = "WebsiteName")
    String websiteName;

    @Column(name = "Logo")
    String logo;

    @Column(name = "Phone")
    String phone;

    @Column(name = "Email")
    String email;

    @Column(name = "Address")
    String address;

    @Column(name = "Copyright")
    String copyright;

    @Column(name = "Facebook")
    String facebook;

    @Column(name = "Twitter")
    String twitter;

    @Column(name = "Youtube")
    String youtube;

    @Column(name = "Instagram")
    String instagram;

    @Column(name = "Policy1", columnDefinition = "MEDIUMTEXT")
    String policy1;

    @Column(name = "Policy2",columnDefinition = "MEDIUMTEXT")
    String policy2;

    @Column(name = "Policy3",columnDefinition = "MEDIUMTEXT")
    String policy3;

    @Column(name = "Policy4",columnDefinition = "MEDIUMTEXT")
    String policy4;

    @Column(name = "Policy5",columnDefinition = "MEDIUMTEXT")
    String policy5;

    @Column(name = "Policy6",columnDefinition = "MEDIUMTEXT")
    String policy6;

    @Column(name = "support1",columnDefinition = "MEDIUMTEXT")
    String support1;

    @Column(name = "support2",columnDefinition = "MEDIUMTEXT")
    String support2;

    @Column(name = "support3",columnDefinition = "MEDIUMTEXT")
    String support3;

    @Column(name = "support4", columnDefinition = "MEDIUMTEXT")
    String support4;

    @Column(name = "support5",columnDefinition = "MEDIUMTEXT")
    String support5;

    @Column(name = "support6",columnDefinition = "MEDIUMTEXT")
    String support6;



}

