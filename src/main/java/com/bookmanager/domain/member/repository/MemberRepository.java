package com.bookmanager.domain.member.repository;

import com.bookmanager.common.MemberStatus;
import com.bookmanager.domain.member.entity.Member;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    // 이메일로 회원 조회
    Optional<Member> findByEmail(String email);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 이름으로 회원 검색 (부분 일치)
    Page<Member> findByNameContaining(String name, Pageable pageable);

    // 회원 상태로 조회
    Page<Member> findByStatus(MemberStatus status, Pageable pageable);

    // 이메일과 상태로 회원 조회
    Optional<Member> findByEmailAndStatus(String email, MemberStatus status);

    // 특정 기간에 가입한 회원 조회
    @Query("SELECT m FROM Member m WHERE m.createdAt BETWEEN :startDate AND :endDate")
    Page<Member> findByCreatedAtBetween(@Param("startDate") Instant startDate,
                                        @Param("endDate") Instant endDate, Pageable pageable);

    // 상태별 회원 수 집계
    @Query("SELECT m.status, COUNT(m) FROM Member m GROUP by m.status")
    List<Object[]> countByStatus();

    // 활성 회원 수 조회
    long countByStatus(MemberStatus status);

}
