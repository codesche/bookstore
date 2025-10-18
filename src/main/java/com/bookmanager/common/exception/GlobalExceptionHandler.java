package com.bookmanager.common.exception;

import com.bookmanager.common.response.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 클래스
 * @RestControllerAdvice를 사용하여 모든 컨트롤러에서 발생하는 예외를 한 곳에서 처리
 * @Slf4j: 로깅을 위한 Lombok 어노테이션
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 도서를 찾을 수 없는 경우 404 NOT_FOUND 응답 반환
     * @param ex
     * @return
     */
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleBookNotFoundException(BookNotFoundException ex) {
        log.error("BookNotFoundException: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.fail(ex.getMessage(), "BOOK_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * MemberNotFoundException 처리
     * 회원을 찾을 수 없을 때 404 NOT_FOUND 응답 반환
     *
     * @param ex MemberNotFoundException
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMemberNotFoundException(MemberNotFoundException ex) {
        log.error("MemberNotFoundException: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.fail(ex.getMessage(), "MEMBER_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * DuplicateResourceException 처리
     * 중복된 리소스가 존재할 때 409 CONFLICT 응답 반환
     *
     * @param ex DuplicateResourceException
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResourceException(DuplicateResourceException ex) {
        log.error("DuplicateResourceException: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.fail(ex.getMessage(), "DUPLICATE_RESOURCE");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * IllegalStateException 처리
     * 비즈니스 로직 위반 시 400 BAD_REQUEST 응답 반환
     * (예: 재고 부족, 상태 변경 불가 등)
     *
     * @param ex IllegalStateException
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        log.error("IllegalStateException: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.fail(ex.getMessage(), "ILLEGAL_STATE");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * IllegalArgumentException 처리
     * 잘못된 인자가 전달되었을 때 400 BAD_REQUEST 응답 반환
     *
     * @param ex IllegalArgumentException
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.fail(ex.getMessage(), "ILLEGAL_ARGUMENT");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * MethodArgumentNotValidException 처리
     * @Valid 검증 실패 시 400 BAD_REQUEST 응답 반환
     * 검증 실패한 필드와 에러 메시지를 Map으로 반환
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity<ApiResponse<Map<String, String>>>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
        MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: {}", ex.getMessage());

        // 검증 실패한 필드와 에러 메시지를 Map으로 수집
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.fail(
            "입력값 검증에 실패했습니다.",
            "VALIDATION_FAILED",
            errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 그 외 모든 예외 처리
     * 예상하지 못한 예외 발생 시 500 INTERNAL_SERVER_ERROR 응답 반환
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("예상치 못한 예외가 발생했습니다: ", ex);

        ApiResponse<Void> response = ApiResponse.fail(
            "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
            "INTERNAL_SERVER_ERROR"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
