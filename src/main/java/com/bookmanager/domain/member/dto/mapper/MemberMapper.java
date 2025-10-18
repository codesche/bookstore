package com.bookmanager.domain.member.dto.mapper;

import com.bookmanager.domain.member.dto.request.MemberRequest;
import com.bookmanager.domain.member.dto.request.MemberUpdateRequest;
import com.bookmanager.domain.member.dto.response.MemberResponse;
import com.bookmanager.domain.member.entity.Member;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface MemberMapper {

    /**
     * MemberRequest → Member Entity 변환
     *
     * @param request MemberRequest DTO
     * @param memberId 생성할 Member의 ID (UUID v7)
     * @param encodedPassword 암호화된 비밀번호
     * @return Member Entity
     */
    @Mapping(target = "memberId", source = "memberId")
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Member toEntity(MemberRequest request, String memberId, String encodedPassword);

    /**
     * Member Entity → MemberResponse 변환
     *
     * @Mapping(target = "status", expression = "..."):
     * - Enum을 String으로 변환 시 name() 메서드 사용
     *
     * @param member Member Entity
     * @return MemberResponse DTO
     */
    @Mapping(target = "status", expression = "java(member.getStatus().name())")
    MemberResponse toResponse(Member member);

    /**
     * MemberUpdateRequest → Member Entity 필드 업데이트
     *
     * @MappingTarget: 업데이트할 대상 Entity 지정
     * @Mapping(target = "memberId", ignore = true): ID는 변경하지 않음
     * @Mapping(target = "email", ignore = true): 이메일은 변경하지 않음
     * @Mapping(target = "password", ignore = true): 비밀번호는 별도 메서드로 관리
     * @Mapping(target = "status", ignore = true): 상태는 별도 메서드로 관리
     *
     * name과 phone만 업데이트
     * 비밀번호 변경은 서비스 레이어에서 별도로 처리
     *
     * @param updateRequest MemberUpdateRequest DTO
     * @param member 업데이트할 Member Entity
     */
    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(MemberUpdateRequest updateRequest, @MappingTarget Member member);
}
