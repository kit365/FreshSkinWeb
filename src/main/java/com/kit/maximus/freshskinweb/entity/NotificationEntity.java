package com.kit.maximus.freshskinweb.entity;

import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Notification",
        indexes = {
                @Index(name = "idx_notification_order_id", columnList = "order_id"),
                @Index(name = "idx_notification_isread_time", columnList = "is_read, Created_at DESC"),
                @Index(name = "idx_notification_deleted", columnList = "Deleted")
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", insertable = false, updatable = false)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
            @OnDelete(action = OnDeleteAction.SET_NULL)
    UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    ReviewEntity review;

    @Column(name = "message")
    String message;

    @Column(name = "is_read")
    Boolean isRead = false; // Use isRead instead of is_read

    @Column(name = "Created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Date time;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    Status status = Status.ACTIVE;

    @Column(name = "Deleted")
    boolean deleted;
}
