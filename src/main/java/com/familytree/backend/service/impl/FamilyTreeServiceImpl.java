package com.familytree.backend.service.impl;

import com.familytree.backend.dto.request.MemberRequest;
import com.familytree.backend.dto.response.MemberResponse;
import com.familytree.backend.entity.Member;
import com.familytree.backend.repository.MemberRepository;
import com.familytree.backend.dto.request.FamilyTreeRequest;
import com.familytree.backend.dto.response.FamilyTreeResponse;
import com.familytree.backend.entity.FamilyTree;
import com.familytree.backend.repository.FamilyTreeRepository;
import com.familytree.backend.service.FamilyTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok: Tự inject Repository vào constructor
public class FamilyTreeServiceImpl implements FamilyTreeService {

    // 1. Khai báo đúng tên biến repository để dùng thống nhất bên dưới
    private final FamilyTreeRepository repository;
    private final MemberRepository memberRepository;

    @Override
    public FamilyTreeResponse createTree(FamilyTreeRequest request) {
        FamilyTree familyTree = FamilyTree.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        FamilyTree savedTree = repository.save(familyTree);
        return mapToResponse(savedTree);
    }

    @Override
    public List<FamilyTreeResponse> getAllTrees() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FamilyTreeResponse mapToResponse(FamilyTree familyTree) {
        return FamilyTreeResponse.builder()
                .id(familyTree.getId())
                .name(familyTree.getName())
                .description(familyTree.getDescription())
                .createdAt(familyTree.getCreatedAt())
                .build();
    }

    @Override
    public MemberResponse addMember(Long treeId, MemberRequest request) {
        
        FamilyTree tree = repository.findById(treeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cây gia đình với id: " + treeId));

        Member member = Member.builder()
                .familyTree(tree)           
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dob(request.getDob())
                .dod(request.getDod())
                .bio(request.getBio())
                .avatarUrl(request.getAvatarUrl()) 
                .build();

        Member savedMember = memberRepository.save(member);
        return mapToMemberResponse(savedMember);
    }

    @Override
    
    public List<MemberResponse> getMembersByTreeId(Long treeId) {
        return memberRepository.findByFamilyTreeId(treeId).stream()
                .map(this::mapToMemberResponse)
                .collect(Collectors.toList());
    }

    private MemberResponse mapToMemberResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .treeId(member.getFamilyTree().getId()) 
                .fullName(member.getFullName())
                .gender(member.getGender())
                .dob(member.getDob())
                .dod(member.getDod())
                .bio(member.getBio())
                .avatarUrl(member.getAvatarUrl())
                .build();
    }
}