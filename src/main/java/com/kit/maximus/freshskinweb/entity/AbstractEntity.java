package com.kit.maximus.freshskinweb.entity;

import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity {

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status = Status.ACTIVE;

    @Column(name = "deleted")
    boolean deleted;


    //    DeletedAt DATETIME,
//    @Column(name = "delete_at")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date deleteAt;


}
