package com.engdiarytoon.server.like;

import com.engdiarytoon.server.post.Post;
import com.engdiarytoon.server.post.PostService;
import com.engdiarytoon.server.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/like")
public class LikeController {

    private final LikeService likeService;
    private final PostService postService;

    @Autowired
    public LikeController(LikeService likeService, PostService postService) {
        this.likeService = likeService;
        this.postService = postService;
    }

    // Endpoint to like a post
    @PostMapping
    public ResponseEntity<String> likePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        Post post = postService.getPostById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        likeService.likePost(user, post);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post liked successfully");
    }

    // Endpoint to unlike a post
    @DeleteMapping
    public ResponseEntity<String> unlikePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        Post post = postService.getPostById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        likeService.unlikePost(user, post);
        return ResponseEntity.ok("Post unliked successfully");
    }

    // Endpoint to get the like count of a post
    @GetMapping("/count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Long postId) {
        Post post = postService.getPostById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        int likeCount = likeService.getLikeCount(post);
        return ResponseEntity.ok(likeCount);
    }

    // Endpoint to check if the user has liked a post
    @GetMapping("/status")
    public ResponseEntity<Boolean> hasUserLikedPost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        Post post = postService.getPostById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        boolean hasLiked = likeService.hasUserLikedPost(user, post);
        return ResponseEntity.ok(hasLiked);
    }
}