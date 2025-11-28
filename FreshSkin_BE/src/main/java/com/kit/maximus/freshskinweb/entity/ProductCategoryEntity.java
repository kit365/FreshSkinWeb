package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.*;
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
@Table(name = "Category", indexes = {
        @Index(name = "idx_category_title", columnList = "title"),
        @Index(name = "idx_category_slug", columnList = "slug")
})
public class ProductCategoryEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "Title")
    String title;

    @Column(name = "Slug")
    String slug;

    @Column(name = "Description", columnDefinition = "MEDIUMTEXT")
    String description;

    @Column(name = "Position")
    Integer position;

    @Column(name = "feature")
    boolean featured;

    @Column(name = "image")
    @ElementCollection(fetch = FetchType.LAZY)
    List<String> image;

    @ManyToMany(mappedBy = "category", fetch = FetchType.LAZY)
    List<ProductEntity> products = new ArrayList<>();


    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    ProductCategoryEntity parent;


    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            mappedBy = "parent")
    List<ProductCategoryEntity> child = new ArrayList<>();

}