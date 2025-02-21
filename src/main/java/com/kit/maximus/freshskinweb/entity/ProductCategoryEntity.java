package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Category")
public class ProductCategoryEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "Title")
    String title;

    @Column(name = "Slug")
    String slug;

    @Column(name = "Description", columnDefinition = "TEXT")
    String description;

    @Column(name = "Position")
    Integer position;

    @Column(name = "feature")
    boolean featured;

    @Column(name = "image")
    String image;


    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            mappedBy = "category")
//    @JsonIgnoreProperties(value = {"createdAt", "updatedAt","parent", "child"})
    List<ProductEntity> products = new ArrayList<>();


    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    ProductCategoryEntity parent;


    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            mappedBy = "parent")
    List<ProductCategoryEntity> child = new ArrayList<>();


//    public void createProduct(ProductEntity product) {
//        products.add(product);
//        product.setCategory(this);
//    }
//
//    public void removeProduct(ProductEntity product) {
//        products.remove(product);
//        product.setCategory(null);
//    }
//
//    public void createCategory(ProductCategoryEntity productCategory) {
//        productCategory.setParent(this);
//        child.add(productCategory);
//    }
//
//    public void removeCategory(ProductCategoryEntity productCategory) {
//        child.remove(productCategory);
//        productCategory.setParent(null);
//    }


    @Override
    public String toString() {
        return "ProductCategoryEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", description='" + description + '\'' +
                ", position=" + position +
                ", featured=" + featured +
                ", image='" + image + '\'' +
                ", products=" + products +
                ", child=" + child +
                '}';
    }
}