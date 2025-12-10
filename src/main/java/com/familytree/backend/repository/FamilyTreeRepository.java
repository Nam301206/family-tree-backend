package com.familytree.backend.repository;

import com.familytree.backend.entity.FamilyTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyTreeRepository extends JpaRepository<FamilyTree, Long> {
    // có thể đinh nghĩa thêm hàm  tìm kiếm tùy chỉnh ở đây nếu cần
    // list<FamilyTree> findByNameContaining(String namePart);
    
}