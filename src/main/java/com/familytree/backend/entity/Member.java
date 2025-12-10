package com.familytree.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "members")
@Data // lombook: tự sinh getter, setter, toString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // liên kết với cây gia phả (một cây có nhiều thành viên)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tree_id", nullable = false)
    private FamilyTree familyTree;

    @Column(nullable = false)
    private String fullName;

    // lưu trữ giới tính dưới dạng chuỗi
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate dob; // ngày sinh
    private LocalDate dod; // ngày mất (nếu có)

    @Column(columnDefinition = "TEXT")
    private String bio; // biography = tiểu sử
    private String photoUrl; // URL ảnh đại diện

    // Tọa độ hiển thị trên giao diện cây
    private Integer posX;
    private Integer posY;
}
