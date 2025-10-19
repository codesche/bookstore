package com.bookmanager.domain.member.controller;

import com.bookmanager.common.MemberStatus;
import com.bookmanager.common.response.ApiResponse;
import com.bookmanager.domain.member.dto.request.MemberRequest;
import com.bookmanager.domain.member.dto.request.MemberUpdateRequest;
import com.bookmanager.domain.member.dto.response.MemberResponse;
import com.bookmanager.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 가입
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> registerMember(
        @Valid @RequestBody MemberRequest request) {
        log.info("회원 가입 API 호출 - Email: {}", request.getEmail());

        MemberResponse response = memberService.registerMember(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("회원 가입이 완료되었습니다.", response));
    }

    // 회원 ID로 단건 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(
        @PathVariable String memberId) {
        log.info("회원 조회 API 호출 - ID: {}", memberId);

        MemberResponse response = memberService.getMemberById(memberId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 이메일로 회원 조회
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberByEmail(
        @PathVariable String email) {
        log.info("회원 조회 API 호출 - Email: {}", email);

        MemberResponse response = memberService.getMemberByEmail(email);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 전체 회원 목록 조회 (페이징)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MemberResponse>>> getAllMembers(
        @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
        Pageable pageable) {
        log.info("전체 회원 목록 조회 API 호출");

        Page<MemberResponse> response = memberService.getAllMembers(pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 이름으로 회원 검색 (페이징)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<MemberResponse>>> searchMembersByName(
        @RequestParam String name,
        @PageableDefault(size = 10) Pageable pageable) {
        log.info("회원 이름 검색 API 호출 - Name: {}", name);

        Page<MemberResponse> response = memberService.searchMembersByName(name, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 회원 상태로 조회 (페이징)
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<MemberResponse>>> getMembersByStatus(
        @PathVariable MemberStatus status,
        @PageableDefault(size = 10) Pageable pageable) {
        log.info("회원 상태별 조회 API 호출 - Status: {}", status);

        Page<MemberResponse> response = memberService.getMemberByStatus(status, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 회원 정보 수정
    @PatchMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMember(
        @PathVariable String memberId,
        @Valid @RequestBody MemberUpdateRequest request) {
        log.info("회원 정보 수정 API 호출 - ID: {}", memberId);

        MemberResponse response = memberService.updateMember(memberId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 비밀번호 변경
    @PatchMapping("/{memberId}/password")
    public ResponseEntity<ApiResponse<MemberResponse>> changePassword(
        @PathVariable String memberId,
        @Valid @RequestBody MemberUpdateRequest request) {
        log.info("비밀번호 변경 API 호출 - ID: {}", memberId);

        MemberResponse response = memberService.changePassword(memberId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 회원 탈퇴
    @PatchMapping("/{memberId}/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawMember(@PathVariable String memberId) {
        log.info("회원 탈퇴 API 호출 - ID: {}", memberId);

        memberService.withdrawMember(memberId);

        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
    }

    // 회원 활성화
    @PatchMapping("/{memberId}/activate")
    public ResponseEntity<ApiResponse<MemberResponse>> activateMember(
        @PathVariable String memberId) {
        log.info("회원 활성화 API 호출 - ID: {}", memberId);

        MemberResponse response = memberService.activateMember(memberId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 회원 비활성화
    @PatchMapping("/{memberId}/deactivate")
    public ResponseEntity<ApiResponse<MemberResponse>> deactivateMember(
        @PathVariable String memberId) {
        log.info("회원 비활성화 API 호출 - ID: {}", memberId);

        MemberResponse response = memberService.deactivateMember(memberId);

        return ResponseEntity.ok(ApiResponse.success("회원이 비활성화되었습니다." ,response));
    }

    // 회원 완전 삭제
    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable String memberId) {
        log.info("회원 완전 삭제 API 호출 - ID: {}", memberId);

        memberService.deleteMember(memberId);

        return ResponseEntity.ok(ApiResponse.success("회원이 완전히 삭제되었습니다."));
    }

    // 활성 회원 수 조회
    @GetMapping("/count/active")
    public ResponseEntity<ApiResponse<Long>> getActiveMemberCount() {
        log.info("활성 회원 수 조회 API 호출");

        long count = memberService.getActiveMemberCount();

        return ResponseEntity.ok(ApiResponse.success(count));
    }

}
