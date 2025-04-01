//package com.kit.maximus.freshskinweb.entity;
//
//import com.kit.maximus.freshskinweb.utils.DiscountType;
//import com.kit.maximus.freshskinweb.utils.Status;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@Setter
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@ToString
//@Table(name = "Discounts")
//public class DiscountEntity extends AbstractEntity{
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "DiscountId")
//    private String discountId;
//
//    @Column(name = "Name",nullable = false)
//    private String name; // Tên chương trình giảm giá
//
//    @Column(name = "DiscountPercentage",nullable = false)
//    private Double discountPercentage; // Giảm giá theo %
//    @Column(name = "DiscountAmount",nullable = false)
//    private Double discountAmount; // Giảm giá số tiền cố định
//    @Column(name = "MaxDiscount")
//    private Double maxDiscount; // Giảm tối đa (nếu có)
//
//    @Column(name = "StartDate",nullable = false)
//    private Date startDate;
//
//    @Column(name = "EndDate", nullable = false)
//    private Date endDate;
//
//    @Column(name = "UsageLimit")
//    private Integer usageLimit; // Số lần tối đa được sử dụng
//    @Column(name = "Used")
//    private Integer used = 0; // Số lần đã dùng
//
//    @Column(name = "IsGlobal")
//    private Boolean isGlobal = false; // Giảm giá áp dụng toàn bộ hay chỉ một số sản phẩm
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "DiscountType")
//    private DiscountType discountType; // Kiểu giảm giá (PERCENTAGE / FIXED)
//
//
//
//    @OneToMany(mappedBy = "discount", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ProductEntity> products = new ArrayList<>();
//
//    public boolean isValid() {
//        Date now = new Date();
//        return this.getStatus() == Status.ACTIVE && now.after(startDate) && now.before(endDate);
//    }
//
//}
