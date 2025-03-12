package com.example.newsfeed.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateRequestDto {

    private String username;
    private String bio;

    @NotBlank(message = "현재 비밀번호를 입력해야 합니다.")
    private String oldPassword;

    @NotBlank(message = "새 비밀번호를 입력해야 합니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(regexp = ".*\\d.*", message = "비밀번호는 숫자를 포함해야 합니다.")
    @Pattern(regexp = ".*[A-Z].*", message = "비밀번호는 대문자를 포함해야 합니다.")
    @Pattern(regexp = ".*[!@#$%^&*()].*", message = "비밀번호는 특수문자를 포함해야 합니다.")
    private String newPassword;
}