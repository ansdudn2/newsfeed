package com.example.newsfeed.domain.feed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedRequestDto {

    @NotBlank(message = "제목을 입력해야 합니다.")
    private String title;

    @Size(max = 100)
    @NotBlank(message = "내용을 입력해야 합니다.")
    private String content;
}
