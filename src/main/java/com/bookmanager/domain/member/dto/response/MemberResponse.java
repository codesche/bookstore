package com.bookmanager.domain.member.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/**
 * 회원 조회 응답 DTO
 * 서버에서 클라이언트로 회원 정보를 전달하는 객체
 *
 * Entity를 직접 반환하지 않고 DTO로 변환하여 반환
 * (보안, 성능, 유지보수성 향상)
 *
 * 주의: 보안을 위해 password는 포함하지 않음
 */
@Getter
@Builder
public class MemberResponse {

    private String memberId;
    private String email;
    private String name;
    private String phone;
    private String status;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Seoul")
    private Instant createdAt;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Seoul")
    private Instant updatedAt;

}
