package com.engdiarytoon.server.post;

import com.engdiarytoon.server.like.LikeService;
import com.engdiarytoon.server.user.User;
import com.engdiarytoon.server.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final LikeService likeService;

    @Autowired
    public PostController(PostService postService, LikeService likeService) {
        this.postService = postService;
        this.likeService = likeService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Map<String, Object> request, @AuthenticationPrincipal User writer) {
        String title = (String)  request.get("title");
        String content = (String)  request.get("content");
        Boolean isPublic = (Boolean)  request.get("isPublic");
        String weather = (String)  request.get("weather");

        Post post = postService.createPost(title, content, writer.getUserId(), isPublic, weather);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> findByUser(@PathVariable Long userId) {
        List<Post> posts = postService.findByUser(userId);
        List<PostResponse> responses = posts.stream()
                .map(post -> new PostResponse(
                        post.getPostId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getWriter().getUserId(),
                        post.getIsPublic(),
                        post.getWeather(),
                        post.getCreatedAt(),
                        likeService.getLikeCount(post)  // Get like count for each post
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // Retrieve all public posts
    @GetMapping("/public")
    public ResponseEntity<List<PostResponse>> findAllWithPublic() {
        List<Post> posts = postService.findAllWithPublic();
        List<PostResponse> responses = posts.stream()
                .map(post -> new PostResponse(
                        post.getPostId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getWriter().getUserId(),
                        post.getIsPublic(),
                        post.getWeather(),
                        post.getCreatedAt(),
                        likeService.getLikeCount(post)  // Get like count for each post
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // 특정 일기 반환
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        int likeCount = likeService.getLikeCount(post);
        PostResponse response = new PostResponse(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getWriter().getUserId(),
                post.getIsPublic(),
                post.getWeather(),
                post.getCreatedAt(),
                likeCount
        );

        return ResponseEntity.ok(response);
    }

    // Update a post (only the author can update)
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody Map<String, String> updates, @AuthenticationPrincipal User writer) {
        String title = updates.get("title");
        String content = updates.get("content");

        Post updatedPost = postService.updatePost(postId, title, content);
        return ResponseEntity.ok(updatedPost);
    }

    // Delete a post (only the author can delete)
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }
}