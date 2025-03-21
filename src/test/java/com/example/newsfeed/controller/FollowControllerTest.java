package com.example.newsfeed.controller;

import com.example.newsfeed.domain.follow.controller.FollowController;
import com.example.newsfeed.domain.follow.service.FollowService;
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
public class FollowControllerTest {

    @Mock
    private FollowService followService;

    @InjectMocks
    private FollowController followController;

    @Test
    void 팔로우_추가_및_취소_성공() {
        // Given
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        Long followingId = 2L;

        Mockito.when(followService.followUser(Mockito.eq(1L), Mockito.eq(followingId)))
                .thenReturn("팔로우가 되었습니다");

        // When
        ResponseEntity<String> response = followController.followUser(dummyUser, followingId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("팔로우가 되었습니다", response.getBody());
    }
}