package com.kit.maximus.freshskinweb.dataaccess.entity.blogs;

import com.kit.maximus.freshskinweb.dataaccess.entity.AbstractEntity;
import com.kit.maximus.freshskinweb.dataaccess.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Blog", indexes = {
        @Index(name = "idx_blog_title", columnList = "title"),
        @Index(name = "idx_blog_slug", columnList = "slug")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    Long id;

    @Column(name = "Title")
    String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "UserID", nullable = true)
//    @JsonIgnore
    UserEntity user;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    String content;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "thumbnail")
    List<String> thumbnail;

    @Column(name = "position")
    Integer position;

    @Column(name = "Slug")
    String slug;

    @Column(name = "Featured")
    boolean featured;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "BlogCategoryId", nullable = true)
    BlogCategoryEntity blogCategory;
}
