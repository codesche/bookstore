package com.bookmanager.domain.book.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

// 수정 시에는 ISBN과 재고 수량을 변경하지 않음

@Getter
@Builder
public class BookUpdateRequest {

    @NotBlank(message = "도서 제목은 필수입니다.")
    @Size(max = 200, message = "도서 제목은 200자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "저자는 필수입니다.")
    @Size(max = 100, message = "저자는 100자를 초과할 수 없습니다.")
    private String author;

    @Size(max = 100, message = "출판사는 100자를 초과할 수 없습니다.")
    private String publisher;

    @NotNull(message = "가격은 필수입니다.")
    @Positive(message = "가격은 양수여야 합니다.")
    private Integer price;

    @Size(max = 2000, message = "도서 설명은 2000자를 초과할 수 없습니다.")
    private String description;

    @Size(max = 50, message = "카테고리는 50자를 초과할 수 없습니다.")
    private String category;

}
