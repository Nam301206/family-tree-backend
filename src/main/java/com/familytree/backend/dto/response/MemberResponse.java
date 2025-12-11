package com.familytree.backend.dto.response;

// file nay dùng để trả về thông tin cho frondend/ postman khi có yêu cầu lấy thông tin thành viên
import lombok.Data;
import java.time.LocalDate;
import com.familytree.backend.entity.Gender;
import lombok.Builder;

@Data
@Builder


public class MemberResponse {
    private Long id;
    private Long treeId; // để biết thuộc cây gia đình nào
    private String fullName;
    private Gender gender;
    private LocalDate dob; // ngay sinh
    private LocalDate dod; // ngay mat
    private String bio; // tỉu su
    private String avatarUrl;
    private Integer posX;
    private Integer posY;
}
