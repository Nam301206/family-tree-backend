package com.familytree.backend.controller;

import com.familytree.backend.dto.request.MemberRequest;
import com.familytree.backend.dto.response.MemberResponse;
import com.familytree.backend.dto.request.FamilyTreeRequest;
import com.familytree.backend.dto.response.FamilyTreeResponse;
//import com.familytree.backend.entity.FamilyTree;
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
    public ResponseEntity<FamilyTreeResponse> createTree(@RequestBody FamilyTreeRequest request) {
        // Lưu ý: Thực tế nên dùng DTO cho request body thay vì Entity trực tiếp
        return ResponseEntity.ok(treeService.createTree(request));
    }

    @GetMapping
    public ResponseEntity<List<FamilyTreeResponse>> getAllTrees() {
        return ResponseEntity.ok(treeService.getAllTrees());
    }

    @PostMapping("/{treeId}/members")
    public ResponseEntity<MemberResponse> addMember(@PathVariable Long treeId, @RequestBody MemberRequest request) {
        return ResponseEntity.ok(treeService.addMember(treeId, request));
    }

    @GetMapping("/{treeId}/members")
    public ResponseEntity<List<MemberResponse>> getMembersByTreeId(@PathVariable Long treeId) {
        return ResponseEntity.ok(treeService.getMembersByTreeId(treeId));
    }
}
