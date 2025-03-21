package com.example.newsfeed.domain.comment.controller;

import com.example.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.example.newsfeed.domain.comment.dto.response.CommentResponseDto;
import com.example.newsfeed.domain.comment.service.CommentService;
import com.example.newsfeed.domain.common.annotation.Auth;
import com.example.newsfeed.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{feedId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long feedId,
                                                            @Auth User user,
                                                            @RequestBody @Valid CommentRequestDto requestDto) {
        CommentResponseDto response = commentService.createComment(feedId, user, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 댓글 조회
    @GetMapping("/{feedId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long feedId) {
        List<CommentResponseDto> comments = commentService.getComments(feedId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정 API
    @PutMapping("/{feedId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long feedId,
                                                            @PathVariable Long commentId,
                                                            @Auth User user,
                                                            @RequestBody @Valid CommentRequestDto requestDto) {
        CommentResponseDto response = commentService.updateComment(feedId, commentId, user, requestDto);
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제 API
    @DeleteMapping("/{feedId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long feedId,
                                                @PathVariable Long commentId,
                                                @Auth User user) {
        commentService.deleteComment(feedId, commentId, user);
        return ResponseEntity.ok("댓글삭제가 완료되었습니다.");
    }
}
