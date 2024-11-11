package com.engdiarytoon.server.post;

import java.time.LocalDateTime;

public class PostResponse {
    private Long postId;
    private String title;
    private String shortContent;
        private Long userId;    // writer
    private Boolean isPublic;
    private String weather;
    private LocalDateTime createdAt;
    private int likeCount;

    private String imageUrl;

    public PostResponse(Long postId, String title, String content, Long userId, Boolean isPublic, String weather, LocalDateTime createdAt, int likeCount, String imageUrl) {
        this.postId = postId;
        this.title = title;
        this.shortContent = content.length() > 250 ? content.substring(0, 250) + "..." : content;
        this.userId = userId;
        this.isPublic = isPublic;
        this.weather = weather;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.imageUrl = imageUrl;
    }

}
