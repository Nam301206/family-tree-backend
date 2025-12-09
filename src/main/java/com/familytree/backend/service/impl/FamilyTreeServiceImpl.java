package com.familytree.backend.service.impl;

import com.familytree.backend.entity.FamilyTree;
import com.familytree.backend.repository.FamilyTreeRepository;
import com.familytree.backend.service.FamilyTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok: Tự inject Repository vào constructor
public class FamilyTreeServiceImpl implements FamilyTreeService {

    private final FamilyTreeRepository repository;

    @Override
    public FamilyTree createTree(String name, String description) {
        FamilyTree tree = FamilyTree.builder()
               .name(name)
               .description(description)
               .build();
        return repository.save(tree);
    }

    @Override
    public List<FamilyTree> getAllTrees() {
        return repository.findAll();
    }
}