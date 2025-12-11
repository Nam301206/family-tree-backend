package com.familytree.backend.dto.request;

// file nay dùng để nhận thông tin từ frontend khi có yêu cầu tạo/ cập nhật thành viên

import com.familytree.backend.entity.Gender;
import lombok.Data;
import java.time.LocalDate;


@Data
public class MemberRequest {
    private String fullName;
    private Gender gender;
    private LocalDate dob; // ngay sinh
    private LocalDate dod; // ngay mat
    private String bio; // tỉu su
    private String avatarUrl;
    private Long treeId; // id của cây gia đình mà thành viên này thuộc về
    private Integer posX;
    private Integer posY;
}
