package com.familytree.backend.dto.response;

import lombok.Data;
import java.util.List;

import com.familytree.backend.entity.Gender;

import java.time.LocalDate;
import java.util.ArrayList;


@Data
public class MemberTreeNode {
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
    private Long fatherId; // id của cha (nếu có)
    private Long motherId; // id của mẹ (nếu có)
    private Long spouseId; // id của vợ/chồng (nếu có)
    private List<MemberTreeNode> children = new ArrayList<>();
}
