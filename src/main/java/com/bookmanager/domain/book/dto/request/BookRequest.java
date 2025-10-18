package com.bookmanager.domain.book.dto.request;

import com.bookmanager.common.BookStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/**
 * 도서 등록 요청 DTO
 * 클라이언트로부터 도서 정보를 받아오는 객체
 *
 * @Valid 어노테이션과 함께 사용하여 요청 데이터 검증
 */
@Getter
@Builder
public class BookRequest {

    // 도서 제목
    @NotBlank(message = "도서 제목은 필수입니다.")
    @Size(max = 200, message = "도서 제목은 200자를 초과할 수 없습니다.")
    private String title;

    // 저자
    @NotBlank(message = "저자는 필수입니다.")
    @Size(max = 100, message = "저자는 100자를 초과할 수 없습니다.")
    private String author;

    // ISBN - 정규식 사용
    @NotBlank(message = "ISBN은 필수입니다.")
    @Pattern(regexp = "^[0-9\\-]+$", message = "ISBN은 숫자와 하이픈만 포함할 수 있습니다.")
    @Size(max = 20, message = "ISBN은 20자를 초과할 수 없습니다.")
    private String isbn;

    // 출판사
    @Size(max = 100, message = "출판사는 100자를 초과할 수 없습니다.")
    private String publisher;

    // 가격 - 양수만 허용
    @NotNull(message = "가격은 필수입니다.")
    @Positive(message = "가격은 양수여야 합니다.")
    private Integer price;

    // 재고 수량 - @PositiveOrZero: 0 또는 양수만 허용
    @NotNull(message = "재고 수량은 필수입니다.")
    @PositiveOrZero(message = "재고 수량은 0 또는 양수여야 합니다.")
    private Integer stockQuantity;

    // 도서 설명
    @Size(max = 2000, message = "도서 설명은 2000자를 초과할 수 없습니다.")
    private String description;

    // 카테고리
    @Size(max = 50, message = "카테고리는 50자를 초과할 수 없습니다.")
    private String category;

    // 도서 상태
    private BookStatus status;

    // 출판일
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Seoul")
    private Instant publishedAt;


}
