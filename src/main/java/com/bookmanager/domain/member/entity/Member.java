package com.bookmanager.domain.member.entity;

import com.bookmanager.common.MemberStatus;
import com.bookmanager.config.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

/**
 * 회원 정보를 관리하는 엔티티 클래스
 *
 * @NoArgsConstructor(access = AccessLevel.PROTECTED): JPA는 기본 생성자가 필요하지만,
 * 외부에서 직접 생성하는 것을 막기 위해 protected로 설정
 *
 * Persistable 인터페이스 구현:
 * - ID를 직접 할당하는 경우, JPA가 새 엔티티인지 기존 엔티티인지 판단하기 위해 SELECT를 실행함
 * - Persistable.isNew()를 구현하여 불필요한 SELECT 방지
 */
@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity implements Persistable<String> {

    /**
     * 회원 고유 ID
     * UUID v7 형식의 문자열로 저장 (시간 정보 포함하여 정렬 가능)
     */
    @Id
    @Column(name = "member_id", nullable = false, length = 36)
    private String memberId;

    /**
     * 이메일 (로그인 ID)
     * 유니크 제약조건으로 중복 방지
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 비밀번호 (암호화 저장)
     * 실제 서비스에서는 BCryptPasswordEncoder 등으로 암호화하여 저장
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * 회원 이름
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 전화번호
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 회원 상태 (ACTIVE, INACTIVE, DELETED)
     */
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    /**
     * 빌더 패턴을 활용한 객체 생성
     */
    @Builder
    public Member(String memberId, String email, String password,
                 String name, String phone, MemberStatus status) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.status = status;
    }

    /**
     * 회원정보 수정 (JPA Dirty Checking 활용)
     */
    public void updateMemberInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 회원 상태 변경
     */
    public void changeStatus(MemberStatus status) {
        this.status = status;
    }

    /**
     * 회원 탈퇴 (상태를 DELETED로 변경)
     * 실제 데이터는 삭제하지 않고 상태만 변경 (Soft Delete)
     */
    public void withdraw() {
        this.status = MemberStatus.DELETED;
    }

    /**
     * 회원 활성화 (상태를 ACTIVE로 변경)
     */
    public void activate() {
        this.status = MemberStatus.ACTIVE;
    }

    /**
     * 회원 비활성화 (상태를 INACTIVE로 변경)
     */
    public void deactivate() {
        this.status = MemberStatus.INACTIVE;
    }

    /**
     * Persistable 인터페이스 구현
     * ID를 반환 (JPA가 엔티티 식별에 사용)
     */
    @Override
    public String getId() {
        return memberId;
    }

    /**
     * Persistable 인터페이스 구현
     * 새 엔티티인지 판단
     *
     * @return createdAt이 null이면 새 엔티티, 아니면 기존 엔티티
     * 이 메서드가 true를 반환하면 JPA는 SELECT 없이 바로 INSERT를 실행
     */
    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }

}
