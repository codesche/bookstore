package com.bookmanager.domain.book.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.bookmanager.common.BookStatus;
import com.bookmanager.common.util.UuidV7Creator;
import com.bookmanager.domain.book.entity.Book;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BookRepository 테스트")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    // 테스트용 도서 데이터
    private Book testBook1;
    private Book testBook2;
    private Book testBook3;

    /**
     * 각 테스트 실행 전에 실행되는 메서드
     * 테스트용 데이터 초기화
     */
    @BeforeEach
    void setUp() {
        testBook1 = Book.builder()
            .bookId(UuidV7Creator.create())
            .title("Effective Java")
            .author("Joshua Bloch")
            .isbn("9780134685991")
            .publisher("Addison-Wesley")
            .price(45000)
            .stockQuantity(50)
            .description("Java 프로그래밍 필독서")
            .category("IT")
            .status(BookStatus.AVAILABLE)
            .publishedAt(Instant.parse("2018-01-01T00:00:00Z"))
            .build();

        testBook2 = Book.builder()
            .bookId(UuidV7Creator.create())
            .title("Clean Code")
            .author("Robert C. Martin")
            .isbn("9780132350884")
            .publisher("Prentice Hall")
            .price(35000)
            .stockQuantity(30)
            .description("클린 코드 작성법")
            .category("IT")
            .status(BookStatus.AVAILABLE)
            .publishedAt(Instant.parse("2008-08-01T00:00:00Z"))
            .build();

        testBook3 = Book.builder()
            .bookId(UuidV7Creator.create())
            .title("Design Patterns")
            .author("Gang of Four")
            .isbn("9780201633612")
            .publisher("Addison-Wesley")
            .price(50000)
            .stockQuantity(0)
            .description("디자인 패턴 바이블")
            .category("IT")
            .status(BookStatus.OUT_OF_STOCK)
            .publishedAt(Instant.parse("1994-10-21T00:00:00Z"))
            .build();

        // 데이터 저장
        bookRepository.save(testBook1);
        bookRepository.save(testBook2);
        bookRepository.save(testBook3);
    }

    @Test
    @DisplayName("도서 저장 테스트")
    void saveBook() {
        // given - 저장할 도서 생성
        Book newBook = Book.builder()
            .bookId(UuidV7Creator.create())
            .title("Spring in Action")
            .author("Craig Walls")
            .isbn("9781617294945")
            .publisher("Manning")
            .price(40000)
            .stockQuantity(20)
            .description("Spring Framework 실전 가이드")
            .category("IT")
            .status(BookStatus.AVAILABLE)
            .publishedAt(Instant.now())
            .build();

        // when - 도서 저장
        Book savedBook = bookRepository.save(newBook);

        // then - 저장된 도서 검증
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getBookId()).isEqualTo(newBook.getBookId());
        assertThat(savedBook.getTitle()).isEqualTo("Spring in Action");
        assertThat(savedBook.getCreatedAt()).isNotNull();
        assertThat(savedBook.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("도서 ID로 조회 테스트")
    void findById() {
        // when - ID로 도서 조회
        Optional<Book> foundBook = bookRepository.findById(testBook1.getBookId());

        // then - 조회 결과 검증
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Effective Java");
        assertThat(foundBook.get().getAuthor()).isEqualTo("Joshua Bloch");
    }

    @Test
    @DisplayName("ISBN으로 도서 조회 테스트")
    void findByIsbn() {
        // when - ISBN으로 도서 조회
        Optional<Book> foundBook = bookRepository.findByIsbn("9780134685991");

        // then - 조회 결과 검증
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Effective Java");
    }

    @Test
    @DisplayName("ISBN 존재 여부 확인 테스트")
    void existsByIsbn() {
        // when & then - 존재하는 ISBN
        assertThat(bookRepository.existsByIsbn("9780134685991")).isTrue();

        // when & then - 존재하지 않는 ISBN
        assertThat(bookRepository.existsByIsbn("0000000000000")).isFalse();
    }

    @Test
    @DisplayName("제목으로 도서 검색 테스트 (페이징)")
    void findByTitleContaining() {
        // given - 페이징 정보
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when - 제목으로 검색 ("Effective"를 포함하는 도서)
        Page<Book> result = bookRepository.findByTitleContaining("Effective", pageRequest);

        // then - 검색 결과 검증
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains("Effective");
    }


}