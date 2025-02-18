package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class BlogEntity extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BlogID", insertable = false, updatable = false)
    Long id;


    @Column(name = "Username")
    String title;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    @JoinColumn(name = "BlogCategoryId")
    BlogCategory blogCategory;

    @Column(name = "content")
    String content;

    @Column(name = "thumbnail")
    String thumbnail;

    @Column(name = "position")
    int position;

    @Column(name = "Featured")
    boolean featured;

}
