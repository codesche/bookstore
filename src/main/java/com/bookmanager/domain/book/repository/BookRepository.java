package com.bookmanager.domain.book.repository;

import com.bookmanager.common.BookStatus;
import com.bookmanager.domain.book.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 도서 정보 Repository
 * JpaRepository 상속받아 기본 CRUD 제공
 *
 * @Repository; Spring Data JPA가 자동으로 구현체 생성
 */
@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    // ISBN으로 도서 조회
    Optional<Book> findByIsbn(String isbn);

    // ISBN 존재 여부 확인
    boolean existsByIsbn(String isbn);

    // 제목으로 도서 검색 (부분 일치)
    Page<Book> findByTitleContaining(String title, Pageable pageable);

    // 저자로 도서 검색 (부분 일치)
    Page<Book> findByAuthorContaining(String author, Pageable pageable);

    // 카테고리로 도서 조회
    Page<Book> findByCategory(String category, Pageable pageable);

    // 도서 상태로 도서 조회
    Page<Book> findByStatus(BookStatus status, Pageable pageable);

    // 가격 범위로 도서 검색
    Page<Book> findByPriceBetween(Integer minPrice, Integer maxPrice, Pageable pageable);

    // 카테고리와 상태로 도서 조회
    Page<Book> findByCategoryAndStatus(String category, BookStatus status, Pageable pageable);

    // 제목 또는 저자로 도서 검색 (복합 검색)
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:title% OR b.author LIKE %:author%")
    Page<Book> searchByTitleOrAuthor(@Param("title") String title, @Param("author") String author, Pageable pageable);

    // 재고가 부족한 도서 조회 (재고 알림용)
    @Query("SELECT b FROM Book b WHERE b.stockQuantity <= :threshold AND b.status = 'AVAILABLE'")
    List<Book> findLowStockBooks(@Param("threshold") int threshold);

    // 카테고리별 도서 수 집계
    @Query("SELECT b.category, COUNT(b) FROM Book b GROUP BY b.category")
    List<Object[]> countByCategory();

}
