package com.example.newsfeed.controller;

import com.example.newsfeed.domain.like.controller.LikeController;
import com.example.newsfeed.domain.like.dto.response.LikeResponseDto;
import com.example.newsfeed.domain.like.service.LikeService;
import com.example.newsfeed.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    @Test
    void 게시물_좋아요_추가_및_취소_성공() {
        // Given
        Long feedId = 1L;
        Mockito.when(likeService.toggleFeedLike(Mockito.any(User.class), Mockito.eq(feedId)))
                .thenReturn("게시물의 좋아요가 추가되었습니다");

        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        // When
        ResponseEntity<LikeResponseDto> response = likeController.likeFeed(dummyUser, feedId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("게시물의 좋아요가 추가되었습니다", response.getBody().getMessage());
    }


    @Test
    void 댓글_좋아요_추가_및_취소_성공() {
        // Given
        Long feedId = 1L, commentId = 1L;
        Mockito.when(likeService.toggleCommentLike(Mockito.any(User.class), Mockito.eq(feedId), Mockito.eq(commentId)))
                .thenReturn("댓글의 좋아요가 추가되었습니다");

        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        // When
        ResponseEntity<LikeResponseDto> response = likeController.likeComment(dummyUser, feedId, commentId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("댓글의 좋아요가 추가되었습니다", response.getBody().getMessage());
    }

}
