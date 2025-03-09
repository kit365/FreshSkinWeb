package com.kit.maximus.freshskinweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Blog")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountEntity extends AbstractEntity {

    @Id
    @Column(name = "PromoCode", columnDefinition = "MEDIUMTEXT")
    String PromoCode;

    @Column(name = "description")
    String Description;

    @Column(name = "DiscountType")
    String DiscountType;

    @Column(name = "v")
    Double DiscountValue;

    @Column(name = "MaxDiscount")
    Double MaxDiscount;

    @Column(name = "UsageLimit")
    Integer UsageLimit;

    @Column(name = "Used")
    Integer Used;
}