package com.kit.maximus.freshskinweb.entity;

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
public class BlogCategory extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BlogCategoryID", insertable = false, updatable = false)
    Long blogCategoryId;

    @Column(name = "Categoryname")
    String blogCategoryName;

    @Column(name = "Description")
    String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "blogCategory", orphanRemoval = true)
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
