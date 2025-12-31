package com.familytree.backend.controller;

import com.familytree.backend.dto.request.MemberRequest;
import com.familytree.backend.dto.response.MemberResponse;
import com.familytree.backend.dto.response.MemberTreeNode;

import lombok.RequiredArgsConstructor;
import com.familytree.backend.service.impl.MemberServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import java.lang.reflect.Member;
//import java.lang.reflect.Member;
import java.util.List;


@RestController
@RequestMapping("/api/v1/members") // đường dẫn gốc cho các API của member
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // cho phép frontend gọi API (tránh lỗi CORS)
public class MemberController {

    private final MemberServiceImpl memberService; // Inject MemberServiceImpl

    //1. API tạo thành viên mới
    // method: POST
    // url: http://localhost:8080/api/members

    @PostMapping()
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest request) {
        MemberResponse newMember = memberService.createMember(request);
        return ResponseEntity.ok(newMember);
    }
    
    // 2. API lấy danh sách thành viên theo cây gia phả
    // method: GET
    // url: http://localhost:8080/api/members/tree/{treeId}
    @GetMapping("/tree/{treeId}")
    public ResponseEntity<List<MemberResponse>> getMembersByTreeId(@PathVariable Long treeId) {
        return ResponseEntity.ok(memberService.getMembersByTreeId(treeId));
    }

    // 2.1 // API Lấy cây gia phả dạng Nested JSON
    // GET: http://localhost:8080/api/v1/members/tree-nested/{treeId}
    @GetMapping("/tree-nested/{treeId}")
    public ResponseEntity<List<MemberTreeNode>> getTreeNested(@PathVariable Long treeId) {
        return ResponseEntity.ok(memberService.getFamilyTreeRecursive(treeId));
    }

    // 2.2 // API Lấy chi tiết 1 thành viên (Để xem profile khi click vào node)
    // GET: http://localhost:8080/api/v1/members/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    // 3. API cập nhập thông tin thành viên
    // method: PUT
    // url: http://localhost:8080/api/members/{id}
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable Long id, @RequestBody MemberRequest request){
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    // 4. API xoá thành viên (nếu cần)
    // method: DELETE
    // url: http://localhost:8080/api/members/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable Long id){
        memberService.deleteMember(id);
        return ResponseEntity.ok("Xoá thành viên thành công");
    }

}
