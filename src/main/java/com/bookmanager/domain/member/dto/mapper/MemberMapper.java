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
     * 여러 파라미터를 사용하므로 default 메서드로 직접 구현
     *
     * @param request MemberRequest DTO
     * @param memberId 생성할 Member의 ID (UUID v7)
     * @param encodedPassword 암호화된 비밀번호
     * @return Member Entity
     */
    default Member toEntity(MemberRequest request, String memberId, String encodedPassword) {
        return Member.builder()
                .memberId(memberId)
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .phone(request.getPhone())
                .status(request.getStatus())
                .build();
    }

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
     * name과 phone만 업데이트
     * 비밀번호 변경은 서비스 레이어에서 Member.changePassword() 메서드를 사용하여 별도로 처리
     * @param updateRequest MemberUpdateRequest DTO
     * @param member 업데이트할 Member Entity
     */
    default void updateEntityFromDto(MemberUpdateRequest updateRequest, Member member) {
        if (updateRequest == null || member == null) {
            return;
        }
        member.updateMemberInfo(updateRequest.getName(), updateRequest.getPhone());
    }
}
