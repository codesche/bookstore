package com.bookmanager.common.exception;

/**
 * 회원을 찾을 수 없을 때 발생하는 예외
 */
public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException() {
        super("회원을 찾을 수 없습니다.");
    }

    public MemberNotFoundException(String message) {
        super(message);
    }

    public static MemberNotFoundException withMemberId(String memberId) {
        return new MemberNotFoundException("회원을 찾을 수 없습니다. (ID: " + memberId + ")");
    }

    public static MemberNotFoundException withEmail(String email) {
        return new MemberNotFoundException("회원을 찾을 수 없습니다. (Email: " + email + ")");
    }

}
