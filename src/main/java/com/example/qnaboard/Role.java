package com.example.qnaboard;

public enum Role {
    GUEST("GUEST"),
    USER("USER"),
    ADMIN("ADMIN");

    String role;

    Role(String role) {
        this.role = role;
    }

    public String value() {
        return role;
    }
}
