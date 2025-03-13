package com.example.newsfeed.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentRequestDto {

    @NotBlank(message = "댓글 내용을 입력해야 합니다.")
    private String content; // 댓글 내용
}
