package com.familytree.backend.controller;

import com.familytree.backend.entity.FamilyTree;
import com.familytree.backend.service.FamilyTreeService; // Import Interface, ko import Impl
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trees")
@RequiredArgsConstructor
public class FamilyTreeController {

    private final FamilyTreeService treeService;

    @PostMapping
    public ResponseEntity<FamilyTree> createTree(@RequestBody FamilyTree request) {
        // Lưu ý: Thực tế nên dùng DTO cho request body thay vì Entity trực tiếp
        return ResponseEntity.ok(treeService.createTree(request.getName(), request.getDescription()));
    }

    @GetMapping
    public ResponseEntity<List<FamilyTree>> getAllTrees() {
        return ResponseEntity.ok(treeService.getAllTrees());
    }
}