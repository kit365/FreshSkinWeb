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
    @Column(name = "BlogCategoryID", insertable = false, updatable = false)
    Long blogCategoryId;

    @Column(name = "Categoryname")
    String blogCategoryName;

    @Column(name = "Description")
    String description;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "blogCategory", orphanRemoval = false)
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
