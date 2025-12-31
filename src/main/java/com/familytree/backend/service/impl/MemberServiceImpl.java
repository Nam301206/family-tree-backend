package com.familytree.backend.service.impl;

import com.familytree.backend.dto.request.MemberRequest;
import com.familytree.backend.dto.response.MemberResponse;
import com.familytree.backend.dto.response.MemberTreeNode;
import com.familytree.backend.entity.FamilyTree;
import com.familytree.backend.entity.Member;
import com.familytree.backend.repository.FamilyTreeRepository;
import com.familytree.backend.repository.MemberRepository;
import com.familytree.backend.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Quan trọng

import java.util.ArrayList;
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
        // validate dữ liệu đầu vào
        validateMemberData(request);

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
        // validate dữ liệu đầu vào
        validateMemberData(request);

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

    private void validateMemberData(MemberRequest request) {
        // Thêm các logic kiểm tra dữ liệu ở đây
        // Ví dụ: kiểm tra tên không được để trống, ngày sinh hợp lệ, v.v.
        // 1.Kiểm tra ngày sinh và ngày mất (nếu có)
        if(request.getDob() != null && request.getDod() != null){
            if(request.getDod().isBefore(request.getDob())){
                throw new RuntimeException("Lỗi: Ngày mất không được trước ngày sinh.");
            }
        }

        //2.1 kiểm tra con với bố(Nếu có chọn bố)
        if(request.getFatherId() != null){
            Member father = memberRepository.findById(request.getFatherId()).orElse(null);
            if(father != null && father.getDob() != null){
                if(request.getDob().isBefore(father.getDob())){
                    throw new RuntimeException("Lỗi logic: Con (" + request.getDob() + ") không thể có ngày sinh trước bố (" + father.getDob() + ").");
                }

                // Nâng cao: kiểm tra khoảng cách tuổi hợp lý (ví dụ: bố ít nhất phải lớn hơn con 15 tuổi)
                if(request.getDob().getYear() - father.getDob().getYear() < 15){
                    throw new RuntimeException("Lỗi logic: Khoảng cách tuổi giữa con và bố phải ít nhất là 15 năm.");
                }
            }
        }

        // 2.2 kiểm tra con với mẹ (Nếu có chọn mẹ)
        if(request.getFatherId() != null){
            Member mother = memberRepository.findById(request.getMotherId()).orElse(null);
            if(mother != null && mother.getDob() != null){
                if(request.getDob().isBefore(mother.getDob())){
                    throw new RuntimeException("Lỗi logic: Con (" + request.getDob() + ") không thể có ngày sinh trước mẹ (" + mother.getDob() + ").");
                }

                // Nâng cao: kiểm tra khoảng cách tuổi hợp lý (ví dụ: mẹ ít nhất phải lớn hơn con 15 tuổi)
                if(request.getDob().getYear() - mother.getDob().getYear() < 15){
                    throw new RuntimeException("Lỗi logic: Khoảng cách tuổi giữa con và mẹ phải ít nhất là 15 năm.");
                }
            }
        }

        // 3. Kiểm tra để trống tên
        if(request.getFullName() == null || request.getFullName().trim().isEmpty()){
            throw new RuntimeException("Lỗi: Tên thành viên không được để trống.");
        }
    }

    // Hàm lấy danh sách dạng cây
    public List<MemberTreeNode> getFamilyTreeRecursive(Long treeId){
        // b1: lấy tất cả thành viên trong cây ra trước
        List<Member> allMembers = memberRepository.findByFamilyTreeId(treeId);

        // b2: tìm những người là gốc (root)
        // là những người không có cha mẹ trong cây
        List<Member> roots = allMembers.stream()
                .filter(m -> m.getFather() == null && m.getMother() == null)
                .collect(Collectors.toList());

        // b3: dùng đệ quy để xây dựng từng cây cho từng người gốc (root)
        List<MemberTreeNode> treeNodes = new ArrayList<>();
        for(Member root: roots){
            treeNodes.add(buildTreeNode(root, allMembers));
        }

        return treeNodes;
    }

    // hàm đệ quy : Xây dựng node cho 1 người và tìm con cháu của họ
    private MemberTreeNode buildTreeNode(Member curentMember, List<Member> allMembers){
        MemberTreeNode node = new MemberTreeNode();
        node.setId(curentMember.getId());
        node.setFullName(curentMember.getFullName());
        node.setGender(curentMember.getGender());
        node.setDob(curentMember.getDob());
        node.setDod(curentMember.getDod());
        node.setBio(curentMember.getBio());
        //node.setAvatarUrl(curentMember.getAvatarUrl());
        node.setPosX(curentMember.getPosX());
        node.setPosY(curentMember.getPosY());
        node.setFatherId(curentMember.getFather() != null ? curentMember.getFather().getId() : null);
        node.setMotherId(curentMember.getMother() != null ? curentMember.getMother().getId() : null);
        node.setSpouseId(curentMember.getSpouse() != null ? curentMember.getSpouse().getId() : null);

        // Tìm con của currentMember
        List<Member> children = allMembers.stream()
                .filter(m -> (m.getFather() != null && m.getFather().getId().equals(curentMember.getId())) ||
                            (m.getMother() != null && m.getMother().getId().equals(curentMember.getId())))
                .collect(Collectors.toList());

        // Gọi đệ quy cho từng đứa con
        for(Member child : children){
            node.getChildren().add(buildTreeNode(child, allMembers));
        }

        return node;
    }

    // Hàm lấy chi tiết 1 thành viên theo ID
    public MemberResponse getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return memberMapper.toResponse(member);
    }
}


    