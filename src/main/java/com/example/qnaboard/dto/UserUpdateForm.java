package com.example.qnaboard.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateForm {
    @NotEmpty(message = "기존 비밀번호를 입력해주세요.")
    private String OriginalPassword;
    @NotEmpty(message = "새 비밀번호를 입력해주세요.")
    private String NewPassword;
    @NotEmpty(message = "새 비밀번호를 확인해주세요.")
    private String NewPasswordCheck;
}
