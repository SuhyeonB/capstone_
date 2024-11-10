package com.engdiarytoon.server.post;

import com.engdiarytoon.server.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto inc
    @Column(name="post_id", nullable = false, updatable = false)
    private Long postId;
    private String title;
    private String content;
    // writer - FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    @Column(name = "isPublic")
    private Boolean isPublic;

    private String weather;

    @Column(name="created_at")
    private LocalDateTime createdAt;
}
