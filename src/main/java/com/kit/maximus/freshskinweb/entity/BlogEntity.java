package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "Blog")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    Long id;

    @Column(name = "Title")
    String title;

    @Column(name = "content",columnDefinition = "MEDIUMTEXT")
    String content;

    @ElementCollection
    @Column(name = "thumbnail")
    List<String> thumbnail;

    @Column(name = "position")
    Integer position;

    @Column(name = "Slug")
    String slug;

    @Column(name = "Featured")
    boolean featured;

    @Column(name = "Author")
    String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "BlogCategoryId", nullable = true)
    BlogCategoryEntity blogCategory;
}
