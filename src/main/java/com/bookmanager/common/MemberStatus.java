package com.bookmanager.common;

// 회원 상태를 나타내는 Status
public enum MemberStatus {

    ACTIVE("활성"),
    INACTIVE("비활성"),
    DELETED("탈퇴");

    private final String description;

    MemberStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
