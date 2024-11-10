package com.engdiarytoon.server.post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post createPost(String title, String content, Long userId, Boolean isPublic, String weather);
    List<Post> findByUser(Long userId);
    List<Post> findAllWithPublic();
    Optional<Post> getPostById(Long postId);
    Post updatePost(Long userId, String title, String content);
    void deletePost(Long postId);
}
