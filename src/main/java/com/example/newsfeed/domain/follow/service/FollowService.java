package com.example.newsfeed.domain.follow.service;

import com.example.newsfeed.domain.feed.entity.Feed;
import com.example.newsfeed.domain.feed.repository.FeedRepository;
import com.example.newsfeed.domain.follow.entity.Follow;
import com.example.newsfeed.domain.follow.repsitory.FollowRepository;
import com.example.newsfeed.domain.user.entity.User;
import com.example.newsfeed.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository, FeedRepository feedRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.feedRepository = feedRepository;
    }

    // 팔로우 추가 또는 취소
    @Transactional
    public String followUser(Long followerId, Long followingId) {
        // 팔로우 대상이 같은 경우 예외 처리
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        // follower와 following을 User 객체로 가져오기
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 이미 팔로우한 관계인지 확인
        Follow existingFollow = followRepository.findByFollowerAndFollowing(follower, following).orElse(null);

        if (existingFollow != null) {
            // 이미 팔로우 중인 관계면 취소 (상태 변경)
            if (existingFollow.getStatus() == Follow.FollowStatus.ACCEPTED) {
                // 팔로우 취소
                existingFollow.setStatus(Follow.FollowStatus.CANCELLED);
                followRepository.save(existingFollow);
                return "팔로우가 취소되었습니다.";
            } else if (existingFollow.getStatus() == Follow.FollowStatus.CANCELLED) {
                // 팔로우 취소 상태일 경우 다시 팔로우 상태로 변경
                existingFollow.setStatus(Follow.FollowStatus.ACCEPTED);
                followRepository.save(existingFollow);
                return "팔로우가 되었습니다.";
            }
        } else {
            // 팔로우 추가
            Follow follow = new Follow(follower, following, Follow.FollowStatus.ACCEPTED);
            followRepository.save(follow);
            return "팔로우가 되었습니다.";
        }

        return "팔로우 취소 또는 상태 변경이 완료되었습니다.";
    }

    // 팔로우한 사람의 최신 게시물 조회
    @Transactional(readOnly = true)
    public Page<Feed> getFeedsByFollowedUsers(Long userId, Pageable pageable) {
        List<Follow> follows = followRepository.findByFollowerId(userId);
        List<Long> followingIds = follows.stream()
                .map(follow -> follow.getFollowing().getId())  // followingId 추출
                .collect(Collectors.toList());

        return feedRepository.findByUserIdIn(followingIds, pageable);
    }


}
