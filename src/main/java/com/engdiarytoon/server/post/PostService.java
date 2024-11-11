package com.engdiarytoon.server.post;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post createPost(String title, String content, Long userId, Boolean isPublic, String weather, String imageUrl);
    List<Post> findByUser(Long userId);
    List<Post> findAllWithPublic();
    List<Post> findTopPostsByCriteria(String sortBy);
    Optional<Post> getPostById(Long postId);
    Post updatePost(Long userId, String title, String content);
    void deletePost(Long postId);

    String storeImage(MultipartFile image);
}
