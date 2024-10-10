package com.example.qnaboard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinForm {
    @NotEmpty(message = "사용자 ID는 필수항목입니다.")
    @Email
    private String email;

    @Size(min = 3, max = 30)
    @NotEmpty(message = "닉네임은 필수항목입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String passwordRepeated;
}
