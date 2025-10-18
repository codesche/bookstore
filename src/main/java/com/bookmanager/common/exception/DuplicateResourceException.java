package com.bookmanager.common.exception;

/**
 * 중복된 리소스가 존재할 때 발생하는 예외
 */
public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException() {
        super("이미 존재하는 리소스입니다.");
    }

    public DuplicateResourceException(String message) {
        super(message);
    }

    public static DuplicateResourceException withIsbn(String isbn) {
        return new DuplicateResourceException("이미 등록된 ISBN입니다. (ISBN: " + isbn + ")");
    }

    public static DuplicateResourceException withEmail(String email) {
        return new DuplicateResourceException("이미 사용 중인 이메일입니다. (Email: " + email + ")");
    }

}
