package com.familytree.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "family_trees")
@Data // lombook: tự sinh getter, setter, toString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FamilyTree {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // quan hệ với user (owner) - giả sử đã có entity User
    // @ManyToOne (fetch = FetchType.LAZY)
    // @JoinColumn(name = "owner_id", nullable = false)
    // private User owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
