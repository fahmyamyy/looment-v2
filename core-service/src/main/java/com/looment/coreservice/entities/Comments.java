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
@Table(name = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Comments {
    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "parent_id", nullable = false, updatable = false)
    private UUID parentId;

    @Column(nullable = false, updatable = false)
    private String comment;

    @Column(name = "total_likes")
    private Integer totalLikes = 0;

    @Column(name = "total_comments")
    private Integer totalComments = 0;

    @Column(name = "with_attachment", nullable = false)
    private Boolean withAttachment = false;

    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private Users users;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
