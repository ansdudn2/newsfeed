package com.example.newsfeed.domain.user.repository;

import com.example.newsfeed.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자찾는매서드
    Optional<User> findByEmail(String email);

    // 이메일 중복여부
    boolean existsByEmail(String email);
}