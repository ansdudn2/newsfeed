package com.example.newsfeed.domain.feed.service;

import com.example.newsfeed.domain.feed.dto.request.FeedRequestDto;
import com.example.newsfeed.domain.feed.dto.request.FeedUpdateRequestDto;
import com.example.newsfeed.domain.feed.dto.response.FeedLikeCountResponseDto;
import com.example.newsfeed.domain.feed.dto.response.FeedResponseDto;
import com.example.newsfeed.domain.feed.entity.Feed;
import com.example.newsfeed.domain.feed.repository.FeedRepository;
import com.example.newsfeed.domain.user.entity.User;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    // 게시글 작성
    @Transactional
    public FeedResponseDto createFeed(User user, FeedRequestDto requestDto) {
        Feed feed = new Feed(requestDto.getTitle(), requestDto.getContent(), user);
        feedRepository.save(feed);
        return new FeedResponseDto(feed);
    }

    //페이지네이션&내림차순 적용 조회
    @Transactional(readOnly = true)
    public Page<FeedResponseDto> getAllFeeds(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        return feedRepository.findAll(pageable).map(FeedResponseDto::new);
    }


    // 단일 게시글 조회
    @Transactional
    public FeedResponseDto getFeedById(Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return new FeedResponseDto(feed);
    }

    //게시글 수정
    @Transactional
    public FeedResponseDto updateFeed(User user, Long feedId, FeedUpdateRequestDto requestDto) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 게시글 작성자와 로그인한 사용자가 같은지 확인
        if (!feed.getUser().equals(user)) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }

        // 제목과 내용 업데이트
        feed.update(requestDto.getTitle(), requestDto.getContent());

        return new FeedResponseDto(feed);
    }

    //게시글 삭제
    @Transactional
    public void deleteFeed(User user, Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 게시글 작성자와 로그인한 사용자가 같은지 확인
        if (!feed.getUser().equals(user)) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        }

        feedRepository.delete(feed); // 삭제
    }

    // 수정일자 기준 최신순 정렬
    public Page<Feed> getAllFeeds(Pageable pageable) {
        return feedRepository.findAll(pageable);
    }

    // 좋아요 많은 순 정렬
    public Page<FeedLikeCountResponseDto> getFeedsSortedByLikes(Pageable pageable) {
        return feedRepository.findAllFeedsOrderByLikeCount(pageable);
    }

    // 기간별 게시물 검색
    public Page<Feed> searchFeedsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return feedRepository.findByCreatedAtBetween(startDate, endDate, pageable);
    }
}
