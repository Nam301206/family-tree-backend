package com.familytree.backend.service.impl;

import com.familytree.backend.dto.request.MemberRequest;
import com.familytree.backend.dto.response.MemberResponse;
import com.familytree.backend.entity.FamilyTree;
import com.familytree.backend.entity.Member;
import com.familytree.backend.repository.FamilyTreeRepository;
import com.familytree.backend.repository.MemberRepository;
import com.familytree.backend.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Quan trọng

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok tự tạo constructor cho các field final
public class MemberServiceImpl { // Nếu có Interface thì implements MemberService

    private final MemberRepository memberRepository;
    private final FamilyTreeRepository familyTreeRepository; // Cần cái này để check cây tồn tại
    private final MemberMapper memberMapper;

    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        // 1. Tìm cây gia phả theo ID gửi lên
        // (Giả sử request có field treeId)
        FamilyTree tree = familyTreeRepository.findById(request.getTreeId())
                .orElseThrow(() -> new RuntimeException("Family Tree not found"));

        // 2. Map từ Request sang Entity
        Member member = memberMapper.toEntity(request);
        
        // 3. Gán cây gia phả vào member
        member.setFamilyTree(tree);

        // 4. Lưu vào DB
        Member savedMember = memberRepository.save(member);

        // 5. Trả về Response
        return memberMapper.toResponse(savedMember);
    }
    
    // Hàm lấy danh sách thành viên theo Tree ID
    public List<MemberResponse> getMembersByTreeId(Long treeId) {
        List<Member> members = memberRepository.findByFamilyTreeId(treeId);
        return members.stream()
                .map(memberMapper::toResponse)
                .collect(Collectors.toList());
    }
}