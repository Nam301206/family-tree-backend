package com.familytree.backend.mapper;

import com.familytree.backend.dto.response.MemberResponse;
import com.familytree.backend.dto.request.MemberRequest;
import com.familytree.backend.entity.Member;
//import com.familytree.backend.entity.Gender;
import org.springframework.stereotype.Component;


@Component
public class MemberMapper {

    // chuyển từ MemberRequest sang Member entity (dùng khi tạo mới/ update thành viên)
    public Member toEntity(MemberRequest request) {
        return Member.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dob(request.getDob())
                .dod(request.getDod())
                .bio(request.getBio())
                .avatarUrl(request.getAvatarUrl())
                .posX(request.getPosX())
                .posY(request.getPosY())
                // Note: familyTree sẽ được set trong service sau khi tìm thấy từ database
                .build();
    }

    // chuyển từ Member entity sang MemberResponse (dùng khi trả về dữ liệu cho frontend)
    public MemberResponse toResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .treeId(member.getFamilyTree().getId())  // lấy Id của cây
                .fullName(member.getFullName())
                .gender(member.getGender())
                .dob(member.getDob())
                .dod(member.getDod())
                .bio(member.getBio())
                .avatarUrl(member.getAvatarUrl())
                .posX(member.getPosX())
                .posY(member.getPosY())
                .build();
    }
    
}
