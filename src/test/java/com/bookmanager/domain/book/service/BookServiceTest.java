package com.bookmanager.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bookmanager.common.BookStatus;
import com.bookmanager.common.exception.BookNotFoundException;
import com.bookmanager.common.exception.DuplicateResourceException;
import com.bookmanager.common.util.UuidV7Creator;
import com.bookmanager.domain.book.dto.mapper.BookMapper;
import com.bookmanager.domain.book.dto.request.BookRequest;
import com.bookmanager.domain.book.dto.request.BookUpdateRequest;
import com.bookmanager.domain.book.dto.response.BookResponse;
import com.bookmanager.domain.book.dto.response.BookSummaryResponse;
import com.bookmanager.domain.book.entity.Book;
import com.bookmanager.domain.book.repository.BookRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("BookService 테스트")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;     // MapStruct Mapper Mock 추가

    @InjectMocks
    private BookService bookService;

    // 테스트용 도서 데이터
    private Book testBook;
    private BookRequest testRequest;
    private BookResponse testResponse;
    private BookSummaryResponse testBookSummaryResponse;

    @BeforeEach
    void setUp() {
        // 테스트용 Book 엔티티 생성
        testBook = Book.builder()
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
            .publishedAt(Instant.now())
            .build();

        // 테스트용 Request DTO 생성
        testRequest = BookRequest.builder()
            .title("Effective Java")
            .author("Joshua Bloch")
            .isbn("9780134685991")
            .publisher("Addison-Wesley")
            .price(45000)
            .stockQuantity(50)
            .description("Java 프로그래밍 필독서")
            .category("IT")
            .status(BookStatus.AVAILABLE)
            .publishedAt(Instant.now())
            .build();

        // 테스트용 Response DTO 생성
        testResponse = BookResponse.builder()
            .bookId(testBook.getBookId())
            .title("Effective Java")
            .author("Joshua Bloch")
            .isbn("9780134685991")
            .publisher("Addison-Wesley")
            .price(45000)
            .stockQuantity(50)
            .description("Java 프로그래밍 필독서")
            .category("IT")
            .status("AVAILABLE")
            .statusDescription("판매중")
            .publishedAt(Instant.now())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        // 테스트용 Response DTO 생성
        testBookSummaryResponse = BookSummaryResponse.builder()
            .bookId(testBook.getBookId())
            .title("Effective Java")
            .author("Joshua Bloch")
            .price(45000)
            .stockQuantity(50)
            .category("IT")
            .status("AVAILABLE")
            .build();
    }

    @Test
    @DisplayName("도서 등록 성공 테스트")
    void createdBook_Success() {
        // given - Mock 동작 정의
        given(bookRepository.existsByIsbn(anyString())).willReturn(false);
        given(bookMapper.toEntity(any(BookRequest.class), anyString())).willReturn(testBook);
        given(bookRepository.save(any(Book.class))).willReturn(testBook);
        given(bookMapper.toResponse(any(Book.class))).willReturn(testResponse);

        // when - 도서 등록 실행
        BookResponse response = bookService.createBook(testRequest);

        // then - 결과 검증
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Effective Java");
        assertThat(response.getAuthor()).isEqualTo("Joshua Bloch");

        // Mock 객체의 메서드 호출 검증
        verify(bookRepository, times(1)).existsByIsbn(anyString());
        verify(bookMapper, times(1)).toEntity(any(BookRequest.class), anyString());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookMapper, times(1)).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("도서 등록 실패 테스트 - ISBN 중복")
    void createBook_Fail_DuplicateIsbn() {
        // given - ISBN이 이미 존재하는 경우
        given(bookRepository.existsByIsbn(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> bookService.createBook(testRequest))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("이미 등록된 ISBN입니다.");

        // save 메서드는 호출되지 않아야 함
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("도서 ID로 조회 성공 테스트")
    void getBookById_Success() {
        // given - Mock 동작 정의
        given(bookRepository.findById(anyString())).willReturn(Optional.of(testBook));
        given(bookMapper.toResponse(any(Book.class))).willReturn(testResponse);

        // when - 도서 조회 실행
        BookResponse response = bookService.getBookById(testBook.getBookId());

        // then - 결과 검증
        assertThat(response).isNotNull();
        assertThat(response.getBookId()).isEqualTo(testBook.getBookId());
        assertThat(response.getTitle()).isEqualTo("Effective Java");

        verify(bookRepository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("도서 ID로 조회 실패 테스트 - 존재하지 않는 도서")
    void getBookById_Fail_NotFound() {
        // given - 도서가 존재하지 않는 경우
        given(bookRepository.findById(anyString())).willReturn(Optional.empty());

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> bookService.getBookById("invalid-id"))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessageContaining("도서를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("ISBN으로 도서 조회 성공 테스트")
    void getBookByIsbn_Success() {
        // given - Mock 동작 정의
        given(bookRepository.findByIsbn(anyString())).willReturn(Optional.of(testBook));
        given(bookMapper.toResponse(any(Book.class))).willReturn(testResponse);

        // when
        BookResponse response = bookService.getBookByIsbn(testBook.getIsbn());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsbn()).isEqualTo("9780134685991");
        assertThat(response.getTitle()).isEqualTo("Effective Java");

        verify(bookRepository, times(1)).findByIsbn(anyString());
    }

    @Test
    @DisplayName("전체 도서 목록 조회 테스트 (페이징)")
    void getAllBooks() {
        // given - Mock 페이지 데이터 생성
        List<Book> bookList = Arrays.asList(testBook);
        Page<Book> bookPage = new PageImpl<>(bookList);
        Pageable pageable = PageRequest.of(0, 10);

        given(bookRepository.findAll(pageable)).willReturn(bookPage);
        given(bookMapper.toSummaryResponse(any(Book.class))).willReturn(testBookSummaryResponse);

        // when
        Page<BookSummaryResponse> response = bookService.getAllBooks(pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("Effective Java");

        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("제목으로 도서 검색 테스트")
    void searchBooksByTitle() {
        // given
        List<Book> bookList = Arrays.asList(testBook);
        Page<Book> bookPage = new PageImpl<>(bookList);
        Pageable pageable = PageRequest.of(0, 10);

        given(bookRepository.findByTitleContaining(anyString(), any(Pageable.class)))
            .willReturn(bookPage);

        // when
        Page<BookSummaryResponse> response = bookService.searchBooksByTitle("Effective", pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);

        verify(bookRepository, times(1)).findByTitleContaining(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("도서 정보 수정 성공 테스트")
    void updateBook_Success() {
        // given
        BookUpdateRequest updateRequest = BookUpdateRequest.builder()
            .title("Effective Java 3rd Edition")
            .author("Joshua Bloch")
            .publisher("Addison-Wesley")
            .price(50000)
            .description("Updated description")
            .category("IT")
            .build();

        given(bookRepository.findById(anyString())).willReturn(Optional.of(testBook));

        // updateEntityFromDto가 실제로 Book을 수정하도록 모킹
        doAnswer(invocation -> {
            BookUpdateRequest req = invocation.getArgument(0);
            Book book = invocation.getArgument(1);
            book.updateBookInfo(
                req.getTitle(),
                req.getAuthor(),
                req.getPublisher(),
                req.getPrice(),
                req.getDescription(),
                req.getCategory()
            );
            return null;
        }).when(bookMapper).updateEntityFromDto(any(BookUpdateRequest.class), any(Book.class));

        // 실제 Book 객체의 현재 상태를 반영하여 응답을 생성
        // willAnswer를 사용하면 Mock이 호출될 때 실제 Book 객체의 현재 상태를 읽어서
        // 응답을 생성하므로, updateEntityFromDto가 수정한 값이 반영
        given(bookMapper.toResponse(any(Book.class))).willAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            return BookResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())             // 수정된 값이 반영됨
                .price(book.getPrice())             // 수정된 값이 반영됨
                .description(book.getDescription())  // 수정된 값이 반영됨
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
        });

        // when
        BookResponse response = bookService.updateBook(testBook.getBookId(), updateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Effective Java 3rd Edition");
        assertThat(response.getPrice()).isEqualTo(50000);
        assertThat(response.getDescription()).isEqualTo("Updated description");

        verify(bookRepository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("재고 추가 성공 테스트")
    void addStock_Success() {
        // given
        int originalStock = testBook.getStockQuantity();
        given(bookRepository.findById(anyString())).willReturn(Optional.of(testBook));

        // 실제 Book 객체의 현재 상태를 반영하여 응답 생성
        MockBookResponse();

        // when
        BookResponse response = bookService.addStock(testBook.getBookId(), 10);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStockQuantity()).isEqualTo(originalStock + 10);

        verify(bookRepository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("재고 추가 실패 테스트 - 음수 수량")
    void addStock_Fail_NegativeQuantity() {
        // when & then - 음수는 IllegalArgumentException 발생
        assertThatThrownBy(() -> bookService.addStock(testBook.getBookId(), -10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("양수여야 합니다");
    }

    @Test
    @DisplayName("재고 감소 성공 테스트")
    void removeStock_Success() {
        // given
        int originalStock = testBook.getStockQuantity();
        given(bookRepository.findById(anyString())).willReturn(Optional.of(testBook));

        MockBookResponse();

        // when
        BookResponse response = bookService.removeStock(testBook.getBookId(), 10);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStockQuantity()).isEqualTo(originalStock - 10);

        verify(bookRepository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("재고 감소 실패 테스트 - 재고 부족")
    void removeStock_Fail_InsufficientStock() {
        // given
        given(bookRepository.findById(anyString())).willReturn(Optional.of(testBook));

        // when & then - 재고보다 많은 수량 감소 시 IllegalStateException 발생
        assertThatThrownBy(() -> bookService.removeStock(testBook.getBookId(), 100))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("재고가 부족합니다");
    }

    @Test
    @DisplayName("도서 상태 변경 테스트")
    void changeBookStatus() {
        // given
        given(bookRepository.findById(anyString())).willReturn(Optional.of(testBook));

        MockBookResponse();

        // when
        BookResponse response = bookService.changeBookStatus(
            testBook.getBookId(),
            BookStatus.OUT_OF_STOCK
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("OUT_OF_STOCK");

        verify(bookRepository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("도서 삭제 성공 테스트")
    void deleteBook_Success() {
        // given
        given(bookRepository.findById(anyString())).willReturn(Optional.of(testBook));
        doNothing().when(bookRepository).delete(any(Book.class));

        // when
        bookService.deleteBook(testBook.getBookId());

        // then
        verify(bookRepository, times(1)).findById(anyString());
        verify(bookRepository, times(1)).delete(any(Book.class));
    }

    @Test
    @DisplayName("도서 삭제 실패 테스트 - 존재하지 않는 도서")
    void deleteBook_Fail_NotFound() {
        // given
        given(bookRepository.findById(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.deleteBook("invalid-id"))
            .isInstanceOf(BookNotFoundException.class);

        // delete 메서드는 호출되지 않아야 함
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    @DisplayName("재고 부족 도서 조회 테스트")
    void getLowStockBooks() {
        // given
        List<Book> lowStockBooks = Arrays.asList(testBook);
        given(bookRepository.findLowStockBooks(anyInt())).willReturn(lowStockBooks);

        // when
        List<BookResponse> response = bookService.getLowStockBooks(10);

        // then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);

        verify(bookRepository, times(1)).findLowStockBooks(anyInt());
    }

    private void MockBookResponse() {
        // 실제 Book 객체의 상태를 반영한 동적 응답
        given(bookMapper.toResponse(any(Book.class))).willAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            return BookResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .price(book.getPrice())
                .stockQuantity(book.getStockQuantity())
                .description(book.getDescription())
                .category(book.getCategory())
                .status(book.getStatus().name())  // 변경된 상태!
                .statusDescription(book.getStatus().getDescription())
                .publishedAt(book.getPublishedAt())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
        });
    }


}