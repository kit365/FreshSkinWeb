package com.kit.maximus.freshskinweb.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Review")
public class ReviewEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReviewID")
    Long reviewId;

    @ManyToOne
    @JoinColumn(name = "ProductID", nullable = false)
    ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    UserEntity user;


    @Column(name = "Rating", nullable = false)
    int rating;

    @Column(name = "Comment", columnDefinition = "TEXT")
    String comment;
}
