package com.familytree.backend.service;

import com.familytree.backend.dto.request.FamilyTreeRequest;
import com.familytree.backend.dto.response.FamilyTreeResponse;
import com.familytree.backend.entity.FamilyTree;
import java.util.List;

public interface FamilyTreeService {
    // định nghĩa hàm tạo cây
    FamilyTreeResponse createTree(FamilyTreeRequest request);

    // định nghĩa hàm lấy danh sách cây
    List<FamilyTreeResponse> getAllTrees();

}
