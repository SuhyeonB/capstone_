package com.engdiarytoon.server.post;

import java.time.LocalDateTime;

public class PostResponse {
    private Long postId;
    private String title;
    private String content;
        private Long userId;    // writer
    private Boolean isPublic;
    private String weather;
    private LocalDateTime createdAt;
    private int likeCount;

    public PostResponse(Long postId, String title, String content, Long userId, Boolean isPublic, String weather, LocalDateTime createdAt, int likeCount) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.isPublic = isPublic;
        this.weather = weather;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
    }

}
