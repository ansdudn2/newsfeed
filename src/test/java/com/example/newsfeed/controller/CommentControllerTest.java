package com.example.newsfeed.controller;

import com.example.newsfeed.domain.comment.controller.CommentController;
import com.example.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.example.newsfeed.domain.comment.dto.response.CommentResponseDto;
import com.example.newsfeed.domain.comment.service.CommentService;
import com.example.newsfeed.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @Test
    void 댓글_작성_성공() {
        // Given
        Long feedId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto("댓글 내용입니다");
        CommentResponseDto responseDto = new CommentResponseDto(
                1L,
                "댓글 내용입니다",
                "testuser",
                LocalDateTime.parse("2025-03-14T01:00:00"),
                LocalDateTime.parse("2025-03-14T01:00:00")
        );
        Mockito.when(commentService.createComment(
                eq(feedId),
                any(User.class),
                any(CommentRequestDto.class)
        )).thenReturn(responseDto);

        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        // When
        ResponseEntity<CommentResponseDto> response = commentController.createComment(feedId, dummyUser, requestDto);

        // Then: HTTP 상태는 201 (Created)이어야 함
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("댓글 내용입니다", response.getBody().getContent());
    }


    @Test
    void 댓글_조회_성공() {
        // Given
        Long feedId = 1L;
        List<CommentResponseDto> comments = Arrays.asList(
                new CommentResponseDto(1L, "댓글1", "user1", LocalDateTime.parse("2025-03-14T01:00:00"), LocalDateTime.parse("2025-03-14T01:00:00")),
                new CommentResponseDto(2L, "댓글2", "user2", LocalDateTime.parse("2025-03-14T01:00:00"), LocalDateTime.parse("2025-03-14T01:00:00"))
        );
        Mockito.when(commentService.getComments(Mockito.eq(feedId))).thenReturn(comments);

        // When
        ResponseEntity<List<CommentResponseDto>> response = commentController.getComments(feedId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void 댓글_수정_성공() {
        // Given
        Long feedId = 1L, commentId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto("수정된 댓글");
        CommentResponseDto responseDto = new CommentResponseDto(1L, "수정된 댓글", "testuser", LocalDateTime.parse("2025-03-14T01:00:00"), LocalDateTime.parse("2025-03-14T01:00:00"));
        Mockito.when(commentService.updateComment(
                eq(feedId),
                eq(commentId),
                any(User.class),
                any(CommentRequestDto.class)
        )).thenReturn(responseDto);


        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        // When
        ResponseEntity<CommentResponseDto> response = commentController.updateComment(feedId, commentId, dummyUser, requestDto);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("수정된 댓글", response.getBody().getContent());
    }

    @Test
    void 댓글_삭제_성공() {
        // Given
        Long feedId = 1L, commentId = 1L;
        Mockito.doNothing().when(commentService).deleteComment(Mockito.eq(feedId), Mockito.eq(commentId), Mockito.any(User.class));

        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        // When
        ResponseEntity<String> response = commentController.deleteComment(feedId, commentId, dummyUser);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("댓글삭제가 완료되었습니다.", response.getBody());
    }
}
