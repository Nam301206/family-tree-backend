package com.familytree.backend.service.impl;

import com.familytree.backend.dto.request.MemberRequest;
import com.familytree.backend.dto.response.MemberResponse;
import com.familytree.backend.entity.FamilyTree;
import com.familytree.backend.entity.Member;
import com.familytree.backend.repository.FamilyTreeRepository;
import com.familytree.backend.repository.MemberRepository;
import com.familytree.backend.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Quan trọng

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok tự tạo constructor cho các field final
public class MemberServiceImpl { // Nếu có Interface thì implements MemberService

    private final MemberRepository memberRepository;
    private final FamilyTreeRepository familyTreeRepository; // Cần cái này để check cây tồn tại
    private final MemberMapper memberMapper;

    // Tạo thành viên mới
    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        // B1: tìm cây gia phả
        FamilyTree tree = familyTreeRepository.findById(request.getTreeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cây gia đình với id: " + request.getTreeId()));

        // B2: Map từ Request sang Entity
        Member member = memberMapper.toEntity(request);
        member.setFamilyTree(tree);

        // B3: lưu lần 1: để sinh ra id trước (quan trọng cho quan hệ 2 chiều)
        Member saveMember = memberRepository.save(member);

        // B4: cập nhập quan hệ (lúc này đã có id để gán quan hệ 2 chiều)
        updateRelationships(saveMember, request);

        // B5: lưu lần 2: để lần : để cập nhập các mối quan hệ vừa lưu vào DB
        saveMember = memberRepository.save(saveMember);

        // B6: trả về response
        return memberMapper.toResponse(saveMember);
    }
    
    // Đọc danh sách thành viên theo treeId
    public List<MemberResponse> getMembersByTreeId(Long treeId) {
        List<Member> members = memberRepository.findByFamilyTreeId(treeId);
        return members.stream()
                .map(memberMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Thêm thành viên: cập nhập thông tin thành viên
    @Transactional
    public MemberResponse updateMember (Long memberId, MemberRequest request){
        //b1: xem thành viên có tồn tại không
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("không tìm thấy thành viên với id: " + memberId));

        // b2: cập nhập thông tin mới vào member cũ
        memberMapper.updateFromRequest(member, request);

        // b3: cập nhập mối quan hệ cha/mẹ/ vợ/chồng nếu có
        updateRelationships(member, request);

        // b4: lưu xuống database (hàm save của JPA tự hiểu là update nếu id đã tồn tại)
        Member updateMember = memberRepository.save(member);

        // b5: trả về response
        return memberMapper.toResponse(updateMember);
    }

    // Xóa thành viên 
    @Transactional
    public void deleteMember(Long memberId){
        Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new RuntimeException("không tìm thấy thành viên với ID: " + memberId));

        // gỡ quan hệ cha/mẹ/ vợ/chồng của các thành viên khác với thành viên này
        List<Member> childrenAsFather = memberRepository.findByFatherId(memberId);
        for(Member child : childrenAsFather){
            child.setFather(null); // gỡ quan hệ cha
            memberRepository.save(child);
        }

        // tương tự với mẹ
        List<Member> childrenAsMother = memberRepository.findByMotherId(memberId);
        for(Member child : childrenAsMother){
            child.setMother(null); // gỡ quan hệ mẹ
            memberRepository.save(child);
        }

        // Gỡ quan hệ vợ / chồng (người ở lại độc thân)
        if(member.getSpouse() != null){
            Member spouse = member.getSpouse();
            spouse.setSpouse(null); // gỡ người này ra khỏi quan hệ vợ/ chồng
            memberRepository.save(spouse);
        }

        // sau khi dọn dẹp sạch sẽ xoá thành viên
        memberRepository.delete(member);
    }

    // hàm phụ trợ : xử lý gán quan hệ cha/mẹ/ vợ/ chồng
    private void updateRelationships(Member member, MemberRequest request){
        // xử lý cha
        if(request.getFatherId() != null){
            Member father = memberRepository.findById(request.getFatherId())
                    .orElse(null); // nếu không tìm thấy thì thôi
            member.setFather(father);
        } else{
            member.setFather(null); // nếu Request gửi null thì xóa cha
    }

        // xử lý mẹ
        if(request.getMotherId() != null){
            Member mother = memberRepository.findById(request.getMotherId())
                    .orElse(null);
            member.setMother(mother);
        } else{
            member.setMother(null);
        }

        // xử lý vợ/ chồng (LoGIC 2 chiều)
        if(request.getSpouseId() != null){
            // Trường Hợp : gán vợ/ chồng mới
            Member newSpouse = memberRepository.findById(request.getMotherId()).orElse(null);
            
            if(newSpouse != null){
                // nếu 1 người đã có vợ/ chồng cũ (không phải là mình), cần gỡ ra trước(tránh đa thê, đa phu)
                if(newSpouse.getSpouse() != null && !newSpouse.getSpouse().getId().equals(member.getId())){
                    Member exOfNewSpouse = newSpouse.getSpouse();
                    exOfNewSpouse.setSpouse(null);
                    memberRepository.save(exOfNewSpouse);
                }

                // Thiết lập quan hệ 2 chiều
                member.setSpouse(newSpouse); // mình trỏ tới họ
                newSpouse.setSpouse(member); // họ trỏ về mình
                memberRepository.save(newSpouse); // lưu người kia
            }
        } else {
            // Trường hợp xóa quan hệ vợ/ chồng
            if(member.getSpouse() != null){
                Member oldSpouse = member.getSpouse();
                oldSpouse.setSpouse(null); // gỡ mình ra khỏi người cũ
                memberRepository.save(oldSpouse); // lưu lại người cư
            }
            member.setSpouse(null); // gỡ ngưỡi cũ ra khỏi mình
        }
    }
}



    