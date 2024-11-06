package com.engdiarytoon.server.post;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="post")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto inc
    @Column(name="user_id", nullable = false, updatable = false)
    private Long postId;
    private String title;
    private String Content;
    // writer - FK
}
