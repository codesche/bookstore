package com.bookmanager.common;

/**
 * 도서 상태 변경
 */
public enum BookStatus {

    AVAILABLE("판매중"),
    OUT_OF_STOCK("품절"),
    DISCONTINUED("절판");

    private final String description;

    BookStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
