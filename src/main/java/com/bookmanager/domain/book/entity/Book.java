package com.bookmanager.domain.book.entity;

import com.bookmanager.common.BookStatus;
import com.bookmanager.config.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 도서 정보를 관리하는 엔티티 클래스
 *
 * @NoArgsConstructor(access = AccessLevel.PROTECTED): JPA는 기본 생성자가 필요하지만,
 * 외부에서 직접 생성하는 것을 막기 위해 protected로 설정
 */
@Entity
@Table(name = "book")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseTimeEntity {

    /**
     * 도서 고유 ID
     * UUID v7 형식의 문자열로 저장 (시간 정보를 포함하여 정렬 가능)
     */
    @Id
    @Column(name = "book_id", nullable = false, length = 36)
    private String bookId;

    /**
     * 도서 제목
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 저자
     */
    @Column(name = "author", nullable = false, length = 100)
    private String author;

    /**
     * ISBN (국제 표준 도서 번호)
     * 유니크 제약조건으로 중복 방지
     */
    @Column(name = "isbn", nullable = false, unique = true, length = 20)
    private String isbn;

    /**
     * 출판사
     */
    @Column(name = "publisher", length = 100)
    private String publisher;

    /**
     * 가격
     */
    @Column(name = "price", nullable = false)
    private Integer price;

    /**
     * 재고 수량
     */
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    /**
     * 도서 설명
     * @Lob: Large Object, 긴 텍스트를 저장하기 위해 사용
     */
    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 카테고리
     */
    @Column(name = "category", length = 50)
    private String category;

    /**
     * 도서 상태 (AVAILABLE, OUT_OF_STOCK, DISCONTINUED)
     * @Enumerated(EnumType.STRING): Enum 값을 문자열로 저장 (ordinal보다 안전)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookStatus status;

    /**
     * 출판일
     */
    @Column(name = "published_at")
    private Instant publishedAt;

    /**
     * 빌더 패턴을 활용한 객체 생성
     * 필수 필드만 생성자에 포함하고, 선택 필드는 빌더로 설정
     *
     * @param bookId 도서 ID (필수)
     * @param title 도서 제목 (필수)
     * @param author 저자 (필수)
     * @param isbn ISBN (필수)
     * @param publisher 출판사
     * @param price 가격 (필수)
     * @param stockQuantity 재고 수량 (필수)
     * @param description 도서 설명
     * @param category 카테고리
     * @param status 도서 상태
     * @param publishedAt 출판일
     */
    @Builder
    public Book(String bookId, String title, String author, String isbn,
        String publisher, Integer price, Integer stockQuantity,
        String description, String category, BookStatus status,
        Instant publishedAt) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.price = price;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
        this.description = description;
        this.category = category;
        this.status = status != null ? status : BookStatus.AVAILABLE;
        this.publishedAt = publishedAt;
    }

    /**
     * 도서 정보 수정 (JPA Dirty Checking 활용)
     * 트랜잭션 내에서 호출하면 자동으로 UPDATE 쿼리 실행
     * @param title 수정할 제목
     * @param author 수정할 저자
     * @param publisher 수정할 출판사
     * @param price 수정할 가격
     * @param description 수정할 설명
     * @param category 수정할 카테고리
     */
    public void updateBookInfo(String title, String author, String publisher,
                                Integer price, String description, String category) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
        this.description = description;
        this.category = category;
    }

    /**
     * 재고 수량 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
        // 재고가 1개 이상이면 상태를 available로 변경
        if (this.stockQuantity > 0 && this.status == BookStatus.OUT_OF_STOCK) {
            this.status = BookStatus.AVAILABLE;
        }
    }

    /**
     * 재고 수량 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stockQuantity = restStock;

        // 재고가 0이 되면 상태를 OUT_OF_STOCK 으로 변경
        if (this.stockQuantity == 0) {
            this.status = BookStatus.OUT_OF_STOCK;
        }
    }

    /**
     * 도서 상태 변경
     */
    public void changeStatus(BookStatus status) {
        this.status = status;
    }

}
