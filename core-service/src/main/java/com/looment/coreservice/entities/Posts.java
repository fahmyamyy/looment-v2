package com.looment.coreservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Posts {
    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String caption;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Boolean commentable = Boolean.TRUE;

    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private Users users;

    @Column(name = "total_likes")
    private Integer totalLikes = 0;

    @Column(name = "total_comments")
    private Integer totalComments = 0;

    @Column(name = "total_views")
    private Integer totalViews = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
