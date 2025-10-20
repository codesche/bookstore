package com.bookmanager.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.bookmanager.common.MemberStatus;
import com.bookmanager.common.util.UuidV7Creator;
import com.bookmanager.domain.member.entity.Member;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MemberRepository 테스트")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    // 테스트용 회원 데이터
    private Member testMember1;
    private Member testMember2;
    private Member testMember3;

    @BeforeEach
    void setUp() {
        testMember1 = Member.builder()
            .memberId(UuidV7Creator.create())
            .email("hong@test.com")
            .password("password123")
            .name("홍길동")
            .phone("010-1234-5678")
            .status(MemberStatus.ACTIVE)
            .build();

        testMember2 = Member.builder()
            .memberId(UuidV7Creator.create())
            .email("kim@test.com")
            .password("password456")
            .name("김철수")
            .phone("010-2345-6789")
            .status(MemberStatus.ACTIVE)
            .build();

        testMember3 = Member.builder()
            .memberId(UuidV7Creator.create())
            .email("lee@test.com")
            .password("password789")
            .name("이영희")
            .phone("010-3456-7890")
            .status(MemberStatus.INACTIVE)
            .build();

        // 데이터 저장
        memberRepository.save(testMember1);
        memberRepository.save(testMember2);
        memberRepository.save(testMember3);
    }

    @Test
    @DisplayName("회원 저장 테스트")
    void saveMember() {
        // given - 저장할 회원 생성
        Member member = Member.builder()
            .memberId(UuidV7Creator.create())
            .email("park@test.com")
            .password("password999")
            .name("박민수")
            .phone("010-9999-9999")
            .status(MemberStatus.ACTIVE)
            .build();

        // when - 회원 저장
        Member savedMember = memberRepository.save(member);

        // then - 저장된 회원 검증
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(savedMember.getEmail()).isEqualTo("park@test.com");
        assertThat(savedMember.getCreatedAt()).isNotNull();
        assertThat(savedMember.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("회원 ID로 조회 테스트")
    void findById() {
        // given - ID로 회원 조회
        Optional<Member> foundMember = memberRepository.findById(testMember1.getMemberId());

        // then - 조회 결과 검증
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("홍길동");
        assertThat(foundMember.get().getEmail()).isEqualTo("hong@test.com");
    }

    @Test
    @DisplayName("이메일로 회원 조회 테스트")
    void findByEmail() {
        // given - 이메일로 회원 조회
        Optional<Member> foundMember = memberRepository.findByEmail(testMember1.getEmail());

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 테스트")
    void existsByEmail() {
        // when & then - 존재하는 이메일
        assertThat(memberRepository.existsByEmail(testMember1.getEmail())).isTrue();

        // when & then - 존재하지 않는 이메일
        assertThat(memberRepository.existsByEmail("nonexist@test.com")).isFalse();
    }

    @Test
    @DisplayName("이름으로 회원 검색 테스트 (페이징)")
    void findByNameContaining() {
        // given - 페이징 정보
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when - 이름으로 검색
        Page<Member> result = memberRepository.findByNameContaining("홍", pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).contains("홍");
    }

    @Test
    @DisplayName("회원 상태로 조회 테스트 (페이징)")
    void findByStatus() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when - ACTIVE 상태 회원 조회
        Page<Member> result = memberRepository.findByStatus(MemberStatus.ACTIVE, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("이메일과 상태로 회원 조회 테스트")
    void findByEmailAndStatus() {
        // when - 이메일과 상태로 조회
        Optional<Member> foundMember = memberRepository.findByEmailAndStatus("hong@test.com", MemberStatus.ACTIVE);

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getEmail()).isEqualTo("hong@test.com");
        assertThat(foundMember.get().getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("특정 기간에 가입한 회원 조회 테스트")
    void findByCreatedAtBetween() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Instant startDate = Instant.now().minusSeconds(3600);       // 1시간 전
        Instant endDate = Instant.now().plusSeconds(3600);              // 1시간 후

        // when - 기간으로 조회
        Page<Member> result = memberRepository.findByCreatedAtBetween(startDate, endDate, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("상태별 회원 수 집계 테스트")
    void countByStatusGroupBy() {
        // when - 상태별 회원 수 집계
        List<Object[]> result = memberRepository.countByStatus();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);          // ACTIVE, INACTIVE;
    }

    @Test
    @DisplayName("활성 회원 수 조회 테스트")
    void countByStatusActive() {
        // when - ACTIVE 상태 회원 수 조회
        long count = memberRepository.countByStatus(MemberStatus.ACTIVE);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("회원 정보 수정 테스트")
    void updateMemberInfo() {
        // given - 수정할 회원 조회
        Member member = memberRepository.findById(testMember1.getMemberId())
            .orElseThrow();

        String originalName = member.getName();

        // when - 회원 정보 수정 (Dirty Checking)
        member.updateMemberInfo("홍길동 수정", "010-9999-8888");
        memberRepository.flush();           // 영속성 컨텍스트 변경사항 DB 반영

        // then - 수정된 회원 검증
        Member updatedMember = memberRepository.findById(testMember1.getMemberId()).orElseThrow();
        assertThat(updatedMember.getName()).isNotEqualTo(originalName);
        assertThat(updatedMember.getName()).isEqualTo("홍길동 수정");
        assertThat(updatedMember.getPhone()).isEqualTo("010-9999-8888");
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    void changePassword() {
        // given
        Member member = memberRepository.findById(testMember1.getMemberId())
            .orElseThrow();

        String originalPassword = member.getPassword();

        // when - 비밀번호 변경
        member.changePassword("newPassword999");
        memberRepository.flush();

        // then
        Member updatedMember = memberRepository.findById(testMember1.getMemberId()).orElseThrow();
        assertThat(updatedMember.getPassword()).isNotEqualTo(originalPassword);
        assertThat(updatedMember.getPassword()).isEqualTo("newPassword999");
    }

    @Test
    @DisplayName("회원 탈퇴 테스트 (Soft Delete)")
    void withdrawMember() {
        // given
        Member member = memberRepository.findById(testMember1.getMemberId())
            .orElseThrow();

        // when - 회원 탈퇴
        member.withdraw();
        memberRepository.flush();

        // then
        Member withdrawnMember = memberRepository.findById(testMember1.getMemberId()).orElseThrow();
        assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.DELETED);
        assertThat(withdrawnMember).isNotNull();
    }

    @Test
    @DisplayName("회원 상태 변경 테스트")
    void changeStatusTest() {
        // given
        Member member = memberRepository.findById(testMember1.getMemberId()).orElseThrow();

        // when - 상태를 INACTIVE로 변경
        member.deactivate();
        memberRepository.flush();

        // then
        Member updatedMember = memberRepository.findById(testMember1.getMemberId()).orElseThrow();
        assertThat(updatedMember.getStatus()).isEqualTo(MemberStatus.INACTIVE);
    }

    @Test
    @DisplayName("회원 삭제 테스트 - Hard Delete")
    void deleteMember() {
        // given
        String memberId = testMember1.getMemberId();

        // when - 회원 삭제
        memberRepository.deleteById(memberId);

        // then
        Optional<Member> deletedMember = memberRepository.findById(memberId);
        assertThat(deletedMember).isEmpty();
    }

    @Test
    @DisplayName("전체 회원 조회 수 테스트")
    void countAllMembers() {
        // when
        long count = memberRepository.count();

        // then
        assertThat(count).isEqualTo(3);
    }


}