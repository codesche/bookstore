package com.bookmanager.domain.book.service;

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
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 도서 비즈니스 로직을 처리하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    /**
     * 도서 등록
     */
    @Transactional
    public BookResponse createBook(BookRequest request) {
        log.info("도서 등록 시작 - ISBN: {}", request.getIsbn());

        // ISBN 중복 체크
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw DuplicateResourceException.withIsbn(request.getIsbn());
        }

        // UUID v7 생성 (시간 정보 포함, 정렬 가능)
        String bookId = UuidV7Creator.create();

        // MapStruct를 사용한 DTO -> Entity 변환
        Book book = bookMapper.toEntity(request, bookId);
        Book savedBook = bookRepository.save(book);

        log.info("도서 등록 완료 - ID: {}, Title: {}", savedBook.getBookId(), savedBook.getTitle());

        // MapStruct를 사용한 Entity + DTO 변환
        return bookMapper.toResponse(savedBook);
    }

    /**
     * 도서 ID로 단건 조회
     */
    public BookResponse getBookById(String bookId) {
        log.info("도서 조회 - ID: {}", bookId);

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> BookNotFoundException.withBookId(bookId));

        return bookMapper.toResponse(book);
    }

    /**
     * ISBN으로 도서 조회
     */
    public BookResponse getBookByIsbn(String isbn) {
        log.info("도서 조회 - ISBN: {}", isbn);

        Book book = bookRepository.findByIsbn(isbn)
            .orElseThrow(() -> BookNotFoundException.withIsbn(isbn));

        return bookMapper.toResponse(book);
    }

    /**
     * 전체 도서 목록 조회 (페이징)
     */
    public Page<BookSummaryResponse> getAllBooks(Pageable pageable) {
        log.info("전체 도서 목록 조회 - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());

        // Page 객체의 map 메서드를 사용하여 Entity를 DTO로 변환
        return bookRepository.findAll(pageable)
            .map(bookMapper::toSummaryResponse);
    }

    /**
     * 제목으로 도서 검색 (페이징)
     */
    public Page<BookSummaryResponse> searchBooksByTitle(String title, Pageable pageable) {
        log.info("도서 제목 검색 - Title: {}", title);

        return bookRepository.findByTitleContaining(title, pageable)
            .map(bookMapper::toSummaryResponse);
    }

    /**
     * 저자로 도서 검색
     */
    public Page<BookSummaryResponse> searchBooksByAuthor(String author, Pageable pageable) {
        log.info("도서 저자 검색 - Author: {}", author);

        return bookRepository.findByAuthorContaining(author, pageable)
            .map(bookMapper::toSummaryResponse);
    }

    /**
     * 카테고리로 도서 조회 (페이징)
     */
    public Page<BookSummaryResponse> getBooksByCategory(String category, Pageable pageable) {
        log.info("카테고리별 도서 조회 - Category: {}", category);

        return bookRepository.findByCategory(category, pageable)
            .map(bookMapper::toSummaryResponse);
    }

    // 도서 정보 수정
    @Transactional
    public BookResponse updateBook(String bookId, BookUpdateRequest request) {
        log.info("도서 정보 수정 - ID : {}", bookId);

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> BookNotFoundException.withBookId(bookId));

        bookMapper.updateEntityFromDto(request, book);

        log.info("도서 정보 수정 완료 - ID: {}", bookId);

        return bookMapper.toResponse(book);
    }

    // 재고 수량 추가
    @Transactional
    public BookResponse addStock(String bookId, int quantity) {
        log.info("재고 추가 - ID: {}, Quantity: {}", bookId, quantity);

        if (quantity <= 0) {
            throw new IllegalArgumentException("추가할 재고 수량은 양수여야 합니다.");
        }

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> BookNotFoundException.withBookId(bookId));

        book.addStock(quantity);

        log.info("재고 추가 완료 - ID: {}, New Stock: {}", bookId, book.getStockQuantity());

        return bookMapper.toResponse(book);
    }

    // 재고 감소
    @Transactional
    public BookResponse removeStock(String bookId, int quantity) {
        log.info("재고 감소 - ID: {}, Quantity: {}", bookId, quantity);
        if (quantity <= 0) {
            throw new IllegalArgumentException("감소할 재고 수량은 양수여야 합니다.");
        }

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> BookNotFoundException.withBookId(bookId));

        book.removeStock(quantity);     // 내부에서 재고 부족 검증
        log.info("재고 감소 완료 - ID: {}, Quantity: {}", bookId, book.getStockQuantity());
        return bookMapper.toResponse(book);
    }

    // 상태 변경
    @Transactional
    public BookResponse changeBookStatus(String bookId, BookStatus status) {
        log.info("도서 상태 변경 - ID: {}, Status: {}", bookId, status);

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> BookNotFoundException.withBookId(bookId));

        book.changeStatus(status);
        log.info("도서 상태 변경 완료 - ID: {}", bookId);
        return bookMapper.toResponse(book);
    }

    // 도서 삭제
    @Transactional
    public void deleteBook(String bookId) {
        log.info("도서 삭제 - ID: {}", bookId);
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> BookNotFoundException.withBookId(bookId));
        bookRepository.delete(book);
        log.info("도서 삭제 완료 - ID: {}", bookId);
    }

    // 재고 부족 도서 조회
    public List<BookResponse> getLowStockBooks(int threshold) {
        log.info("재고 부족 도서 조회 - Threshold: {}", threshold);
        return bookRepository.findLowStockBooks(threshold)
            .stream()
            .map(bookMapper::toResponse)
            .toList();
    }

}
