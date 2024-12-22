package com.engdiarytoon.server.post;

import com.engdiarytoon.server.like.LikeService;
import com.engdiarytoon.server.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<String> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("isPublic") Boolean isPublic,
            @RequestParam("weather") String weather,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Long writerId) { // userId를 SecurityContext에서 가져옴

        try{
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                imageUrl = postService.storeImage(image);
            }
            Post post = postService.createPost(title, content, writerId, isPublic, weather, imageUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body("Post created successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create post");
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long postId,
            @RequestBody Map<String, String> updates,
            @AuthenticationPrincipal Long writerId) {

        // 게시물 작성자인지 검증
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getWriter().getUserId().equals(writerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }

        // 게시물 업데이트
        String title = updates.get("title");
        String content = updates.get("content");
        Post updatedPost = postService.updatePost(postId, title, content);

        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long writerId) {

        // 게시물 작성자인지 검증
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getWriter().getUserId().equals(writerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }

        // 게시물 삭제
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
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
                        likeService.getLikeCount(post),  // Get like count for each post
                        post.getImageUrl()
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
                        likeService.getLikeCount(post),  // Get like count for each post
                        post.getImageUrl()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/public/like-counts")
    public ResponseEntity<List<PostResponse>> findAllWithLikeCount() {
        List<Post> posts = postService.findTopPostsByCriteria("likeCount");
        List<PostResponse> responses = posts.stream()
                .map(post -> new PostResponse(
                        post.getPostId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getWriter().getUserId(),
                        post.getIsPublic(),
                        post.getWeather(),
                        post.getCreatedAt(),
                        likeService.getLikeCount(post),
                        post.getImageUrl()
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
                likeCount,
                post.getImageUrl()
        );

        return ResponseEntity.ok(response);
    }
}