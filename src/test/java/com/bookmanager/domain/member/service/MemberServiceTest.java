package com.bookmanager.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("MemberService 테스트")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService memberService;

    // 테스트용 회원 데이터
    private Member testMember;
    private MemberRequest testMemberRequest;
    private MemberUpdateRequest testMemberUpdateRequest;
    private MemberResponse testMemberResponse;

    // 각 테스트 메서드가 독립적으로 실행되도록 보장
    @BeforeEach
    void setUp() {
        // 테스트용 Member 엔티티 생성
        testMember = Member.builder()
            .memberId(UuidV7Creator.create())
            .email("test@exmaple.com")
            .password("encodedPassword123!")
            .name("홍길동")
            .phone("010-1234-5678")
            .status(MemberStatus.ACTIVE)
            .build();

        // 테스트용 회원 가입 RequestDTO
        testMemberRequest = MemberRequest.builder()
            .email("test@example.com")
            .password("Password123!")
            .name("홍길동")
            .phone("010-1234-5678")
            .status(MemberStatus.ACTIVE)
            .build();

        // 테스트용 회원 정보 수정 RequestDTO
        testMemberUpdateRequest = MemberUpdateRequest.builder()
            .name("jonathan")
            .phone("010-9876-5432")
            .currentPassword("Password123!")
            .newPassword("NewPassword123!")
            .passwordConfirm("Password123!")
            .build();

        // 테스트용 ResponseDTO
        testMemberResponse = MemberResponse.builder()
            .memberId(testMember.getMemberId())
            .email("test@example.com")
            .name("홍길동")
            .phone("010-1234-5678")
            .status("ACTIVE")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    // 회원 가입
    @Test
    @DisplayName("회원 가입 성공 테스트")
    void registerMember_Success() {
        // given - Mock 동작 정의
        given(memberRepository.existsByEmail(anyString())).willReturn(false);
        given(memberMapper.toEntity(any(MemberRequest.class), anyString(), anyString()))
            .willReturn(testMember);
        given(memberRepository.save(any(Member.class))).willReturn(testMember);
        given(memberMapper.toResponse(any(Member.class))).willReturn(testMemberResponse);

        // when - 회원 가입 실행
        MemberResponse response = memberService.registerMember(testMemberRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("홍길동");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");

        // Mock 메서드 호출 검증
        verify(memberRepository, times(1)).existsByEmail(anyString());
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(memberMapper, times(1)).toEntity(any(MemberRequest.class), anyString(), anyString());
        verify(memberMapper, times(1)).toResponse(any(Member.class));
    }

    // 회원 가입 실패 - 이메일 중복
    @Test
    @DisplayName("회원 가입 실패 테스트 - 이메일 중복")
    void registerMember_Fail_DuplicateEmail() {
        // given
        given(memberRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.registerMember(testMemberRequest))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("이미 사용 중인");

        // save 메서드는 호출되지 않아야 함
        verify(memberRepository, never()).save(any(Member.class));
    }

    // 회원 ID로 조회 - 성공
    @Test
    @DisplayName("회원 ID로 조회 성공 테스트")
    void getMemberById_Success() {
        // given
        given(memberRepository.findById(anyString())).willReturn(Optional.of(testMember));
        given(memberMapper.toResponse(any(Member.class))).willReturn(testMemberResponse);

        // when
        MemberResponse response = memberService.getMemberById(testMember.getMemberId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMemberId()).isEqualTo(testMember.getMemberId());
        assertThat(response.getEmail()).isEqualTo("test@example.com");

        verify(memberRepository, times(1)).findById(anyString());
        verify(memberMapper, times(1)).toResponse(any(Member.class));
    }

    // 회원 ID 조회 실패 - 회원 미존재
    @Test
    @DisplayName("회원 ID로 조회 실패 테스트 - 존재하지 않는 회원")
    void getMemberById_Fail_NotFound() {
        // given
        given(memberRepository.findById(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMemberById("invalid-id"))
            .isInstanceOf(MemberNotFoundException.class)
            .hasMessageContaining("회원을 찾을 수 없습니다.");

        verify(memberRepository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("이메일로 회원 조회 성공 테스트")
    void getMemberByEmail_Success() {
        // given
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(testMember));
        given(memberMapper.toResponse(any(Member.class))).willReturn(testMemberResponse);

        // when
        MemberResponse response = memberService.getMemberByEmail("test@example.com");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@example.com");

        verify(memberRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("이메일로 회원 조회 실패 테스트 - 존재하지 않는 이메일")
    void getMemberByEmail_Fail_NotFound() {
        // given
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMemberByEmail("notfound@example.com"))
            .isInstanceOf(MemberNotFoundException.class);

        verify(memberRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("전체 회원 목록 조회 테스트 (페이징)")
    void getAllMembers() {
        // given - Mock 페이지 데이터 생성
        List<Member> memberList = Arrays.asList(testMember);
        Page<Member> memberPage = new PageImpl<>(memberList);
        Pageable pageable = PageRequest.of(0, 10);

        given(memberRepository.findAll(pageable)).willReturn(memberPage);
        given(memberMapper.toResponse(any(Member.class))).willReturn(testMemberResponse);

        // when
        Page<MemberResponse> response = memberService.getAllMembers(pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getEmail()).isEqualTo("test@example.com");

        verify(memberRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("이름으로 회원 검색 테스트")
    void searchMembersByName() {
        // given
        List<Member> memberList = Arrays.asList(testMember);
        Page<Member> memberPage = new PageImpl<>(memberList);
        Pageable pageable = PageRequest.of(0, 10);

        given(memberRepository.findByNameContaining(anyString(), any(Pageable.class)))
            .willReturn(memberPage);
        given(memberMapper.toResponse(any(Member.class))).willReturn(testMemberResponse);

        // when
        Page<MemberResponse> response = memberService.searchMembersByName("홍길동", pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName()).contains("홍길동");

        verify(memberRepository,
            times(1)).findByNameContaining(anyString(), any(Pageable.class));
    }



}