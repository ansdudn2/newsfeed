package com.example.newsfeed.domain.like.service;

import com.example.newsfeed.domain.like.entity.Like;
import com.example.newsfeed.domain.like.repository.LikeRepository;
import com.example.newsfeed.domain.feed.entity.Feed;
import com.example.newsfeed.domain.comment.entity.Comment;
import com.example.newsfeed.domain.user.entity.User;
import com.example.newsfeed.domain.feed.repository.FeedRepository;
import com.example.newsfeed.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;

    // 게시물에 좋아요 추가&취소
    @Transactional
    public String toggleFeedLike(User user, Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        //본인 게시물엔 좋아요불가
        if (feed.getUser().equals(user)) {
            throw new IllegalArgumentException("본인이 작성한 게시물에는 좋아요를 남길 수 없습니다.");
        }

        // 여러 개의 결과가 있을 수 있으므로 List로 반환
        List<Like> existingLikes = likeRepository.findByUserAndFeed(user, feed);

        if (!existingLikes.isEmpty()) {
            // 중복된 좋아요 모두 삭제
            likeRepository.deleteAll(existingLikes);
            return "게시물의 좋아요가 취소되었습니다.";
        } else {
            Like newLike = new Like(user, feed, null); // 피드에 대한 좋아요 추가
            likeRepository.save(newLike);
            return "게시물의 좋아요가 추가되었습니다.";
        }
    }

    @Transactional
    public String toggleCommentLike(User user, Long feedId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // feedId와 일치하는지 확인
        Feed feed = comment.getFeed();
        if (feed.getId() != feedId) {
            throw new IllegalArgumentException("댓글이 해당 게시물에 속하지 않습니다.");
        }

        //본인 댓글엔 좋아요 불가
        if (comment.getUser().equals(user)) {
            throw new IllegalArgumentException("본인이 작성한 댓글에는 좋아요를 남길 수 없습니다.");
        }

        // 여러 개의 결과가 있을 수 있으므로 List로 반환
        List<Like> existingLikes = likeRepository.findByUserAndComment(user, comment);

        if (!existingLikes.isEmpty()) {
            // 중복된 좋아요 모두 삭제
            likeRepository.deleteAll(existingLikes);
            return "댓글의 좋아요가 취소되었습니다.";
        } else {
            Like newLike = new Like(user, feed, comment); // 댓글에 대한 좋아요 추가
            likeRepository.save(newLike);
            return "댓글의 좋아요가 추가되었습니다.";
        }
    }
}


