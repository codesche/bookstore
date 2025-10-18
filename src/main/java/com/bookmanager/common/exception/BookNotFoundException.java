package com.bookmanager.common.exception;

/**
 * 도서를 찾을 수 없을 때 발생하는 예외
 * RuntimeException을 상속받아 Unchecked Exception으로 처리
 */
public class BookNotFoundException extends RuntimeException {

    /**
     * 기본 생성자
     */
    public BookNotFoundException() {
        super("도서를 찾을 수 없습니다.");
    }

    /**
     * 메시지를 포함한 생성자
     */
    public BookNotFoundException(String message) {
        super(message);
    }

    /**
     * 도서 ID를 포함한 예외 메시지 생성
     */
    public static BookNotFoundException withBookId(String bookId) {
        return new BookNotFoundException("도서를 찾을 수 없습니다. (ID: " + bookId + ")");
    }

    /**
     * ISBN을 포함한 예외 메시지 생성
     */
    public static BookNotFoundException withIsbn(String isbn) {
        return new BookNotFoundException("도서를 찾을 수 없습니다. (ISBN: " + isbn + ")");
    }

}
