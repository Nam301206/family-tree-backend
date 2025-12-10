package com.familytree.backend.service.impl;

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

    private final FamilyTreeRepository repository;

    @Override
    public FamilyTreeResponse createTree(FamilyTreeRequest request) {
        // 1. Convert DTO -> Entity
        FamilyTree familyTree = FamilyTree.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        // 2. Lưu Entity vào DB
        FamilyTree savedTree = repository.save(familyTree);

        // 3. Convert Entity -> DTO
        return mapToResponse(savedTree);
    }

    @Override
    public List<FamilyTreeResponse> getAllTrees() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Hàm phụ trợ để chuyển đổi dữ liệu Entity -> DTO
    private FamilyTreeResponse mapToResponse(FamilyTree familyTree) {
        return FamilyTreeResponse.builder()
                .id(familyTree.getId())
                .name(familyTree.getName())
                .description(familyTree.getDescription())
                .createdAt(familyTree.getCreatedAt())
                //.updatedAt(familyTree.getUpdatedAt())
                .build();
    }
       
}