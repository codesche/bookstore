package com.bookmanager.domain.book.dto.mapper;

import com.bookmanager.domain.book.dto.request.BookRequest;
import com.bookmanager.domain.book.dto.request.BookUpdateRequest;
import com.bookmanager.domain.book.dto.response.BookResponse;
import com.bookmanager.domain.book.dto.response.BookSummaryResponse;
import com.bookmanager.domain.book.entity.Book;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Book Entity와 DTO 간 변환을 담당하는 Mapper 인터페이스
 *
 * @Mapper: MapStruct가 컴파일 타임에 구현체를 자동 생성
 * - componentModel = "spring": Spring Bean으로 등록 (@Component 자동 적용)
 * - unmappedTargetPolicy = ReportingPolicy.IGNORE: 매핑되지 않은 필드 무시
 * - injectionStrategy = InjectionStrategy.CONSTRUCTOR: 생성자 주입 방식 사용
 *
 * MapStruct의 장점:
 * 1. 컴파일 타임에 매핑 코드 생성 → 런타임 오버헤드 없음
 * 2. 타입 안정성 보장
 * 3. 리플렉션 사용 안 함 → 성능 우수
 * 4. 가독성 좋은 매핑 코드
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BookMapper {

    /**
     * BookRequest → Book Entity 변환
     *
     * @Mapping: 필드명이 다르거나 변환 로직이 필요한 경우 사용
     * - target: Entity의 필드명
     * - source: DTO의 필드명
     * - ignore = true: 해당 필드는 매핑하지 않음
     *
     * @param request BookRequest DTO
     * @param bookId 생성할 Book의 ID (UUID v7)
     * @return Book Entity
     */
    @Mapping(target = "bookId", source = "bookId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Book toEntity(BookRequest request, String bookId);

    /**
     * Book Entity → BookResponse 변환
     *
     * @Mapping(target = "status", expression = "..."):
     * - Enum을 String으로 변환 시 name() 메서드 사용
     *
     * @Mapping(target = "statusDescription", expression = "..."):
     * - Enum의 description을 가져오는 커스텀 로직
     *
     * @param book Book Entity
     * @return BookResponse DTO
     */
    @Mapping(target = "status", expression = "java(book.getStatus().name())")
    @Mapping(target = "statusDescription", expression = "java(book.getStatus().getDescription())")
    BookResponse toResponse(Book book);

    /**
     * Book Entity → BookResponse.Summary 변환
     * 목록 조회 시 사용하는 간단한 응답 DTO
     *
     * @param book Book Entity
     * @return BooksummaryResponse DTO
     */
    @Mapping(target = "status", expression = "java(book.getStatus().name())")
    BookSummaryResponse toSummaryResponse(Book book);

    /**
     * BookRequest.Update → Book Entity 필드 업데이트
     *
     * @MappingTarget: 업데이트할 대상 Entity 지정
     * @Mapping(target = "bookId", ignore = true): ID는 변경하지 않음
     * @Mapping(target = "isbn", ignore = true): ISBN은 변경하지 않음
     * @Mapping(target = "stockQuantity", ignore = true): 재고는 별도 메서드로 관리
     * @Mapping(target = "status", ignore = true): 상태는 별도 메서드로 관리
     *
     * @param updateRequest BookUpdateRequest DTO
     * @param book 업데이트할 Book Entity
     */
    @Mapping(target = "bookId", ignore = true)
    @Mapping(target = "isbn", ignore = true)
    @Mapping(target = "stockQuantity", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(BookUpdateRequest updateRequest, @MappingTarget Book book);


}
