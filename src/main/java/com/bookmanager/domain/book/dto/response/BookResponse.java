package com.bookmanager.domain.book.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/**
 * 도서 조회 응답 DTO
 * 서버에서 클라이언트로 도서 정보를 전달하는 객체
 *
 * Entity를 직접 반환하지 않고 DTO로 변환하여 반환
 * (보안, 성능, 유지보수성 향상)
 */
@Getter
@Builder
public class BookResponse {

    private String bookId;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private Integer price;
    private Integer stockQuantity;
    private String description;
    private String category;
    private String status;
    private String statusDescription;

    /**
     * 출판일
     * @JsonFormat: JSON 직렬화 시 날짜 형식 지정
     */
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Seoul")
    private Instant publishedAt;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Seoul")
    private Instant createdAt;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Seoul")
    private Instant updatedAt;


}
