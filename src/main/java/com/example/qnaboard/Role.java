package com.example.qnaboard;

import lombok.Getter;

@Getter
public enum Role {
    GUEST("ROLE_GUEST"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    Role(String role) {
        this.role = role;
    }
    private String role;
}
