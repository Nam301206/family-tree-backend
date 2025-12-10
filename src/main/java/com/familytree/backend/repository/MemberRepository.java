package com.familytree.backend.repository;

import com.familytree.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // Tìm tất cả thành viên theo ID cây gia đình
    List<Member> findByFamilyTreeId(Long familyTreeId);
}