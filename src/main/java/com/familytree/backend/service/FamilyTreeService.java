package com.familytree.backend.service;

import com.familytree.backend.entity.FamilyTree;
import java.util.List;

public interface FamilyTreeService {
    // định nghĩa hàm tạo cây
    FamilyTree createTree(String name, String description);

    // định nghĩa hàm lấy danh sách cây
    List<FamilyTree> getAllTrees();

}
