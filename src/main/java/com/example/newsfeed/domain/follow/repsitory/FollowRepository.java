package com.example.newsfeed.domain.follow.repsitory;

import com.example.newsfeed.domain.follow.entity.Follow;
import com.example.newsfeed.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 팔로우 관계 확인
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // 특정 followerId에 대한 팔로우 리스트를 조회
    List<Follow> findByFollowerId(Long followerId);
}

