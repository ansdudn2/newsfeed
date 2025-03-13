package com.example.newsfeed.domain.follow.entity;

import com.example.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;  // 팔로우하는 사용자

    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private User following;  // 팔로우 당하는 사용자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowStatus status;  // 팔로우 상태

    public enum FollowStatus {
        ACCEPTED, CANCELLED
    }

    // 팔로우 생성자
    public Follow(User follower, User following, FollowStatus status) {
        this.follower = follower;
        this.following = following;
        this.status = status;
    }

    // 팔로우 상태 변경을 위한 setter 추가
    public void setStatus(FollowStatus status) {
        this.status = status;
    }
}
