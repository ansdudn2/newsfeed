package com.example.newsfeed.service;

import com.example.newsfeed.domain.comment.entity.Comment;
import com.example.newsfeed.domain.comment.repository.CommentRepository;
import com.example.newsfeed.domain.feed.entity.Feed;
import com.example.newsfeed.domain.feed.repository.FeedRepository;
import com.example.newsfeed.domain.like.entity.Like;
import com.example.newsfeed.domain.like.repository.LikeRepository;
import com.example.newsfeed.domain.like.service.LikeService;
import com.example.newsfeed.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    void 게시물_좋아요_추가_성공() {
        // Given
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);


        Feed feed = new Feed("제목", "내용", new User());
        ReflectionTestUtils.setField(feed, "id", 2L);
        User feedUser = new User();
        ReflectionTestUtils.setField(feedUser, "id", 3L);
        ReflectionTestUtils.setField(feedUser, "username", "feedAuthor");
        ReflectionTestUtils.setField(feed, "user", feedUser);


        Mockito.when(feedRepository.findById(eq(2L))).thenReturn(Optional.of(feed));

        Mockito.when(likeRepository.findByUserAndFeed(eq(user), eq(feed)))
                .thenReturn(Collections.emptyList());

        // When
        String result = likeService.toggleFeedLike(user, 2L);

        // Then
        assertEquals("게시물의 좋아요가 추가되었습니다.", result);
        Mockito.verify(likeRepository, Mockito.times(1)).save(any(Like.class));
    }

    @Test
    void 댓글_좋아요_추가_성공() {
        // Given
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        Comment comment = new Comment();
        ReflectionTestUtils.setField(comment, "id", 1L);

        Feed feed = new Feed("제목", "내용", new User());
        ReflectionTestUtils.setField(feed, "id", 2L);

        User commentUser = new User();
        ReflectionTestUtils.setField(commentUser, "id", 3L);
        ReflectionTestUtils.setField(commentUser, "username", "commentAuthor");
        ReflectionTestUtils.setField(comment, "user", commentUser);
        ReflectionTestUtils.setField(comment, "feed", feed);

        Mockito.when(commentRepository.findById(eq(1L))).thenReturn(Optional.of(comment));

        Mockito.when(likeRepository.findByUserAndComment(eq(user), eq(comment)))
                .thenReturn(Collections.emptyList());

        // When
        String result = likeService.toggleCommentLike(user, 2L, 1L);

        // Then
        assertEquals("댓글의 좋아요가 추가되었습니다.", result);
        Mockito.verify(likeRepository, Mockito.times(1)).save(any(Like.class));
    }
}
