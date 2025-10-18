package com.bookmanager.config;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 모든 엔티티의 생성 시간과 수정 시간을 자동으로 관리하는 기본 엔티티 클래스
 * JPA Auditing 기능을 활용하여 자동으로 시간 정보를 설정
 *
 * @MappedSuperClass: 이 클래스는 실제 테이블과 매핑되지 않으며, 상속받는 엔티티에만 속성을 제공
 * @EntityListeners: JPA 라이프사이클 이벤트를 감지하여 자동으로 createdAt, updatedAt 설정
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    /**
     * 엔티티 생성 시간
     * @CreatedDate: 엔티티가 처음 생성될 때 자동으로 현재 시간이 설정됨
     * Instant 타입 사용을 통해 TimeZone 관련 독립적인 시간 관리 (확장성 고려)
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * 엔티티 수정 시간
     * @LastModifiedDate: 엔티티가 수정될 때마다 자동으로 현재 시간으로 업데이트됨
     * Instant 타입 사용으로 TimeZone 관련 독립적인 시간 관리 (확장성 고려)
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}
