package com.bookmanager.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/**
 * 공통 API 응답 객체
 * 모든 API 응답을 일관된 형태로 반환하기 위한 래퍼 클래스
 * @JsonInclude(JsonInclude.Include.NON_NULL): null 값은 JSON 응답에 포함하지 않음
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String errorCode;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Seoul")
    private Instant timestamp;

    /**
     * 성공 응답 생성 (데이터 포함)
     * @param data - 응답 데이터
     * @return ApiResponse<T>
     * @param <T> 데이터 타입
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message("요청이 성공적으로 처리되었습니다.")
            .data(data)
            .timestamp(Instant.now())
            .build();
    }

    /**
     * 성공 응답 생성 (메시지 + 데이터)
     * @param message 응답 메시지
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return ApiResponse<T>
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .timestamp(Instant.now())
            .build();
    }

    /**
     * 성공 응답 생성 (메시지만)
     * @param message 응답 메시지
     * @param <T> 데이터 타입
     * @return ApiResponse<T>
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .timestamp(Instant.now())
            .build();
    }

    /**
     * 실패 응답 생성 (메시지만)
     * @param message 에러 메시지
     * @param <T> 데이터 타입
     * @return ApiResponse<T>
     */
    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .timestamp(Instant.now())
            .build();
    }

    /**
     * 실패 응답 생성 (메시지 + 에러 코드)
     * @param message 에러 메시지
     * @param errorCode 에러 코드
     * @param <T> 데이터 타입
     * @return ApiResponse<T>
     */
    public static <T> ApiResponse<T> fail(String message, String errorCode) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .errorCode(errorCode)
            .timestamp(Instant.now())
            .build();
    }

    /**
     * 실패 응답 생성 (메시지 + 에러 코드 + 데이터)
     * @param message 에러 메시지
     * @param errorCode 에러 코드
     * @param data 에러 상세 정보
     * @param <T> 데이터 타입
     * @return ApiResponse<T>
     */
    public static <T> ApiResponse<T> fail(String message, String errorCode, T data) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .errorCode(errorCode)
            .data(data)
            .timestamp(Instant.now())
            .build();
    }

}
