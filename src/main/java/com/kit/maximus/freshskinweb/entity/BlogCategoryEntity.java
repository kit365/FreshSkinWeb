package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "BlogCategory")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogCategoryEntity extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", insertable = false, updatable = false)
    Long id;

    @Column(name = "title")
    String title;

    @Column(name = "Description",columnDefinition = "MEDIUMTEXT")
    String description;

    @Column(name = "Position")
    Integer position;

    @Column(name = "feature")
    boolean featured;

    @ElementCollection
    @Column(name = "image")
    List<String> image;

    @Column(name = "Slug")
    String slug;

    @JsonIgnore
    @OneToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY, mappedBy = "blogCategory", orphanRemoval = false)
    List<BlogEntity> blog = new ArrayList<>();

    public void createBlog(BlogEntity blogEntity) {
        blog.add(blogEntity);
        blogEntity.setBlogCategory(this);
    }

    public void removeBlog(BlogEntity blogEntity) {
        blog.remove(blogEntity);
        blogEntity.setBlogCategory(null);
    }
}
