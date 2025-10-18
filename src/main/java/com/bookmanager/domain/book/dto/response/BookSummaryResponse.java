package com.bookmanager.domain.book.dto.response;

import com.bookmanager.domain.book.entity.Book;
import lombok.Builder;
import lombok.Getter;

// 도서 조회용 간단한 응답 DTO
@Getter
@Builder
public class BookSummaryResponse {

    private String bookId;
    private String title;
    private String author;
    private Integer price;
    private Integer stockQuantity;
    private String category;
    private String status;

    public static BookSummaryResponse fromBookEntity(Book book) {
        return BookSummaryResponse.builder()
            .bookId(book.getBookId())
            .title(book.getTitle())
            .author(book.getAuthor())
            .price(book.getPrice())
            .stockQuantity(book.getStockQuantity())
            .category(book.getCategory())
            .status(book.getStatus().name())
            .build();
    }

}
