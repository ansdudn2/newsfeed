package com.example.newsfeed.domain.user.entity;

import com.example.newsfeed.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    //자기소개는 100 자까지만가능
    @Column(length =100)
    private String bio;

    public User(String email, String password, String username, String bio) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.bio = (bio != null) ? bio : "";
    }
}

