package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kit.maximus.freshskinweb.utils.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Discount")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountEntity extends AbstractEntity {

    @Id
    @Column(name = "PromoCode",unique = true)
    String promoCode;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    String description;

    @Column(name = "DiscountType")
    String discountType;

    @Column(name = "discountValue")
    Double discountValue;

    @Column(name = "MaxDiscount")
    Double maxDiscount;

    @Column(name = "UsageLimit")
    Integer usageLimit;

    @Column(name = "isGlobal")
    Boolean isGlobal;

    @Column(name = "Used")
    Integer used;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "discountEntity")
    @JsonManagedReference
    List<ProductEntity> productEntities = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "discountEntity")
    @JsonManagedReference
    List<UserDiscountUsageEntity> userDiscountUsageEntities = new ArrayList<>();

    public void addUserDiscountUsageEntity(UserDiscountUsageEntity userDiscountUsageEntity) {
        userDiscountUsageEntities.add(userDiscountUsageEntity);
        userDiscountUsageEntity.setDiscountEntity(this);
    }

    public void removeUserDiscountUsageEntity(UserDiscountUsageEntity userDiscountUsageEntity) {
        userDiscountUsageEntities.remove(userDiscountUsageEntity);
        userDiscountUsageEntity.setDiscountEntity(null);
    }

    public void addProduct(@NotNull ProductEntity productEntity){
        productEntities.add(productEntity);
        productEntity.setDiscountEntity(this);
    }

    public void removeProduct(@NotNull ProductEntity productEntity){
        productEntities.remove(productEntity);
        productEntity.setDiscountEntity(null);
    }
}