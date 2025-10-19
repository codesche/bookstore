package com.bookmanager.domain.member.service;

import com.bookmanager.common.MemberStatus;
import com.bookmanager.common.exception.DuplicateResourceException;
import com.bookmanager.common.exception.MemberNotFoundException;
import com.bookmanager.common.util.UuidV7Creator;
import com.bookmanager.domain.member.dto.mapper.MemberMapper;
import com.bookmanager.domain.member.dto.request.MemberRequest;
import com.bookmanager.domain.member.dto.request.MemberUpdateRequest;
import com.bookmanager.domain.member.dto.response.MemberResponse;
import com.bookmanager.domain.member.entity.Member;
import com.bookmanager.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    // 회원 가입
    @Transactional
    public MemberResponse registerMember(MemberRequest request) {
        log.info("회원 가입 시작 - Email: {}", request.getEmail());

        // 이메일 중복 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw DuplicateResourceException.withEmail(request.getEmail());
        }

        // UUID v7 생성 (시간 정보 포함, 정렬 가능)
        String memberId = UuidV7Creator.create();

        // 실제 서비스에선 BCryptPasswordEncoder 암호화 필요
        // 여기선 간단히 원본 비밀번호 그대로 사용
        String encodedPassword = request.getPassword();

        // MapStruct를 사용한 DTO -> Entity 반환
        Member member = memberMapper.toEntity(request, memberId, encodedPassword);
        Member savedMember = memberRepository.save(member);

        log.info("회원 가입 완료 - ID: {}, Email: {}", savedMember.getMemberId(), savedMember.getEmail());

        // MapStruct를 사용한 Entity -> DTO 반환
        return memberMapper.toResponse(savedMember);
    }

    // 회원 ID로 단건 조회
    public MemberResponse getMemberById(String memberId) {
        log.info("회원 조회 - ID: {}", memberId);

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(memberId));

        return memberMapper.toResponse(member);
    }

    // 이메일로 회원 조회
    public MemberResponse getMemberByEmail(String email) {
        log.info("회원 조회 - Email: {}", email);

        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> MemberNotFoundException.withEmail(email));

        return memberMapper.toResponse(member);
    }

    // 전체 회원 목록 조회 (페이징)
    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        log.info("전체 회원 목록 조회 - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());

        return memberRepository.findAll(pageable)
            .map(memberMapper::toResponse);
    }

    // 이름으로 회원 검색 (페이징)
    public Page<MemberResponse> searchMembersByName(String name, Pageable pageable) {
        log.info("회원 이름 검색 - Name: {}", name);

        return memberRepository.findByNameContaining(name, pageable)
            .map(memberMapper::toResponse);
    }

    // 회원 상태로 조회 (페이징)
    public Page<MemberResponse> getMemberByStatus(MemberStatus status, Pageable pageable) {
        log.info("회원 상태별 조회 - Status: {}", status);

        return memberRepository.findByStatus(status, pageable)
            .map(memberMapper::toResponse);
    }

    // 회원 정보 수정
    @Transactional
    public MemberResponse updateMember(String memberId, MemberUpdateRequest request) {
        log.info("회원 정보 수정 - ID: {}", memberId);

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> MemberNotFoundException.withMemberId(memberId));

        // MapStruct를 사용한 DTO -> Entity 업데이트
        memberMapper.updateEntityFromDto(request, member);

        log.info("회원 정보 수정 완료 - ID:{}", memberId);

        return memberMapper.toResponse(member);
    }

    // 비밀번호 변경
    @Transactional
    public MemberResponse changePassword(String memberId, MemberUpdateRequest request) {
        log.info("비밀번호 변경 - ID: {}", memberId);

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> MemberNotFoundException.withMemberId(memberId));

        // 실제 서비스에서는 현재 비밀번호 확인 필요
        // if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
        //     throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        // }

        // 새 비밀번호 확인
        if (!request.getNewPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        // 실제: String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        String encodedPassword = request.getNewPassword();

        member.changePassword(encodedPassword);

        log.info("비밀번호 변경 완료 - ID: {}", memberId);

        return memberMapper.toResponse(member);
    }

    // 회원 탈퇴 (Soft Delete)
    @Transactional
    public void withdrawMember(String memberId) {
        log.info("회원 탈퇴 - ID: {}", memberId);

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> MemberNotFoundException.withMemberId(memberId));

        member.withdraw();

        log.info("회원 탈퇴 완료 - ID: {}", memberId);
    }

    // 회원 활성화
    @Transactional
    public MemberResponse activateMember(String memberId) {
        log.info("회원 활성화 - ID: {}", memberId);

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> MemberNotFoundException.withMemberId(memberId));

        member.activate();

        log.info("회원 활성화 완료 - ID: {}", memberId);

        return memberMapper.toResponse(member);
    }

    // 회원 비활성화
    @Transactional
    public MemberResponse deactivateMember(String memberId) {
        log.info("회원 비활성화 - ID: {}", memberId);

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> MemberNotFoundException.withMemberId(memberId));

        member.deactivate();

        log.info("회원 비활성화 완료 - ID: {}", memberId);

        return memberMapper.toResponse(member);
    }

    // 회원 완전 삭제
    // 실제 데이터를 DB에서 삭제
    @Transactional
    public void deleteMember(String memberId) {
        log.info("회원 완전 삭제 - ID: {}", memberId);

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> MemberNotFoundException.withMemberId(memberId));

        memberRepository.delete(member);

        log.info("회원 완전 삭제 완료 - ID: {}", memberId);
    }

    // 활성 회원 수 조회
    public long getActiveMemberCount() {
        return memberRepository.countByStatus(MemberStatus.ACTIVE);
    }

}
