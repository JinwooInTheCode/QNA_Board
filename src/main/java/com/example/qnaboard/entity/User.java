package com.example.qnaboard.entity;

import com.example.qnaboard.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "USER_TABLE")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String email;
    // 회원 닉네임
    @Column(unique = true)
    private String username;
    // 회원 본명
    private String name;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String provider;
    private String providerId;
    private String refreshToken;
}
