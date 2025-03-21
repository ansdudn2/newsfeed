package com.example.newsfeed.service;

import com.example.newsfeed.domain.follow.entity.Follow;
import com.example.newsfeed.domain.follow.repsitory.FollowRepository;
import com.example.newsfeed.domain.follow.service.FollowService;
import com.example.newsfeed.domain.user.entity.User;
import com.example.newsfeed.domain.user.repository.UserRepository;
import com.example.newsfeed.domain.feed.repository.FeedRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowService followService;

    @Test
    void 팔로우_추가_성공() {
        // Given
        Long followerId = 1L;
        Long followingId = 2L;

        User follower = new User();
        ReflectionTestUtils.setField(follower, "id", followerId);
        User following = new User();
        ReflectionTestUtils.setField(following, "id", followingId);

        Mockito.when(userRepository.findById(eq(followerId))).thenReturn(Optional.of(follower));
        Mockito.when(userRepository.findById(eq(followingId))).thenReturn(Optional.of(following));

        Mockito.when(followRepository.findByFollowerAndFollowing(eq(follower), eq(following)))
                .thenReturn(Optional.empty());

        // When
        String message = followService.followUser(followerId, followingId);

        // Then
        assertEquals("팔로우가 되었습니다.", message);
        Mockito.verify(followRepository, Mockito.times(1)).save(any(Follow.class));
    }

    @Test
    void 팔로우_취소_성공() {
        // Given
        Long followerId = 1L;
        Long followingId = 2L;

        User follower = new User();
        ReflectionTestUtils.setField(follower, "id", followerId);
        User following = new User();
        ReflectionTestUtils.setField(following, "id", followingId);

        Mockito.when(userRepository.findById(eq(followerId))).thenReturn(Optional.of(follower));
        Mockito.when(userRepository.findById(eq(followingId))).thenReturn(Optional.of(following));

        Follow existingFollow = new Follow(follower, following, Follow.FollowStatus.ACCEPTED);
        ReflectionTestUtils.setField(existingFollow, "id", 1L);
        Mockito.when(followRepository.findByFollowerAndFollowing(eq(follower), eq(following)))
                .thenReturn(Optional.of(existingFollow));

        // When
        String message = followService.followUser(followerId, followingId);

        // Then
        assertEquals("팔로우가 취소되었습니다.", message);
        Mockito.verify(followRepository, Mockito.times(1)).save(existingFollow);
    }
}
