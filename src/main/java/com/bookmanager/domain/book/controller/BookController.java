package com.bookmanager.domain.book.controller;

import com.bookmanager.common.BookStatus;
import com.bookmanager.common.response.ApiResponse;
import com.bookmanager.domain.book.dto.request.BookRequest;
import com.bookmanager.domain.book.dto.request.BookUpdateRequest;
import com.bookmanager.domain.book.dto.response.BookResponse;
import com.bookmanager.domain.book.dto.response.BookSummaryResponse;
import com.bookmanager.domain.book.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 도서 등록
    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
        @Valid @RequestBody BookRequest request) {
        log.info("도서 등록 API 호출 - Title: {}", request.getTitle());

        BookResponse response = bookService.createBook(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("도서가 성공적으로 등록되었습니다.", response));
    }

    // 도서 ID로 단건 조회
    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(
        @PathVariable String bookId) {
        log.info("도서 조회 API 호출 - ID: {}", bookId);

        BookResponse response = bookService.getBookById(bookId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ISBN으로 도서 조회
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookByIsbn(
        @PathVariable String isbn) {
        log.info("도서 조회 API 호출 - ISBN: {}", isbn);

        BookResponse response = bookService.getBookByIsbn(isbn);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 전체 도서 목록 조회 (페이징)
    // @PageableDefault: 페이징 기본값 설정
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookSummaryResponse>>> getAllBooks(
        @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
        Pageable pageable) {
        log.info("전체 도서 목록 조회 API 호출");

        Page<BookSummaryResponse> response = bookService.getAllBooks(pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 제목으로 도서 검색 (페이징)
    // @RequestParam: URL 쿼리 파라미터에서 값 추출
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<Page<BookSummaryResponse>>> searchBooksByTitle(
        @RequestParam String keyword,
        @PageableDefault(size = 10) Pageable pageable) {
        log.info("도서 제목 검색 API 호출 - Keyword: {}", keyword);

        Page<BookSummaryResponse> response = bookService.searchBooksByTitle(keyword, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 저자로 도서 검색 (페이징)
    @GetMapping("/search/author")
    public ResponseEntity<ApiResponse<Page<BookSummaryResponse>>> searchBooksByAuthor(
        @RequestParam String keyword,
        @PageableDefault(size = 10) Pageable pageable) {
        log.info("도서 저자 검색 API 호출 - Keyword: {}", keyword);

        Page<BookSummaryResponse> response = bookService.searchBooksByAuthor(keyword, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 카테고리로 도서 조회 (페이징)
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<Page<BookSummaryResponse>>> getBooksByCategory(
        @PathVariable String category,
        @PageableDefault(size = 10) Pageable pageable) {
        log.info("카테고리별 도서 조회 API 호출 - Category: {}", category);

        Page<BookSummaryResponse> response = bookService.getBooksByCategory(category, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 도서 정보 수정
    @PatchMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
        @PathVariable String bookId,
        @Valid @RequestBody BookUpdateRequest request) {
        log.info("도서 정보 수정 API 호출 - ID: {}", bookId);

        BookResponse response = bookService.updateBook(bookId, request);

        return ResponseEntity.ok(ApiResponse.success("도서 정보가 수정되었습니다." ,response));
    }

    // 재고 수량 추가
    @PatchMapping("/{bookId}/stock/add")
    public ResponseEntity<ApiResponse<BookResponse>> addStock(
        @PathVariable String bookId,
        @RequestParam int quantity) {
        log.info("재고 추가 API 호출 - ID: {}, Quantity: {}", bookId, quantity);

        BookResponse response = bookService.addStock(bookId, quantity);

        return ResponseEntity.ok(ApiResponse.success("재고가 추가되었습니다.", response));
    }

    // 재고 수량 감소
    @PatchMapping("/{bookId/stock/remove}")
    public ResponseEntity<ApiResponse<BookResponse>> removeStock(
       @PathVariable String bookId,
       @RequestParam int quantity) {
        log.info("재고 감소 API 호출 - ID: {}, Quantity: {}", bookId, quantity);

        BookResponse response = bookService.removeStock(bookId, quantity);

        return ResponseEntity.ok(ApiResponse.success("재고가 감소되었습니다.", response));
    }

    // 도서 상태 변경
    @PatchMapping("/{bookId}/status")
    public ResponseEntity<ApiResponse<BookResponse>> changeBookStatus(
        @PathVariable String bookId,
        @RequestParam BookStatus status) {
        log.info("도서 상태 변경 API 호출 - ID: {}, Status: {}", bookId, status);

        BookResponse response = bookService.changeBookStatus(bookId, status);

        return ResponseEntity.ok(ApiResponse.success("도서 상태가 변경되었습니다.", response));
    }

    // 도서 삭제
    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable String bookId) {
        log.info("도서 삭제 API 호출 - ID: {}", bookId);

        bookService.deleteBook(bookId);

        return ResponseEntity.ok(ApiResponse.success("도서가 삭제되었습니다."));
    }

    // 재고가 부족한 도서 목록 조회
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getLowStockBooks(
        @RequestParam(defaultValue = "10") int threshold) {
        log.info("재고 부족 도서 조회 API 호출 - Threshold: {}", threshold);

        List<BookResponse> response = bookService.getLowStockBooks(threshold);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
