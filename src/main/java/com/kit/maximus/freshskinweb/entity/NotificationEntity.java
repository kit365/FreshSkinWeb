package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Notification")
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

    String message;

    boolean is_read;

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
