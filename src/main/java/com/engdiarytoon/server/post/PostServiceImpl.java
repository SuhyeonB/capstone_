package com.engdiarytoon.server.post;

import com.engdiarytoon.server.like.LikeService;
import com.engdiarytoon.server.user.User;
import com.engdiarytoon.server.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService{

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final LikeService likeService;

    private final Path imageStoragePath = Paths.get("src/main/resources/static/images");


    @Autowired
    public PostServiceImpl(UserRepository userRepository, PostRepository postRepository, LikeService likeService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.likeService = likeService;
    }
    @Override
    public Post createPost(String title, String content, Long userId, Boolean isPublic, String weather, String imageUrl) {
        User writer = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: "));

        Post post = Post.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .isPublic(isPublic)
                .weather(weather)
                .createdAt(LocalDateTime.now())
                .imageUrl(imageUrl)
                .build();
        return postRepository.save(post);
    }

    @Override
    public List<Post> findByUser(Long userId) {
        return postRepository.findByWriterUserId(userId);
    }

    @Override
    public List<Post> findAllWithPublic() {
        return postRepository.findByIsPublicTrue().stream().limit(100).collect(Collectors.toList()); //?
    }

    @Override
    public List<Post> findTopPostsByCriteria(String sortBy) {
        List<Post> posts = postRepository.findByIsPublicTrue();
        if ("likeCount".equals(sortBy)) {
            posts.sort((post1, post2) -> Integer.compare(
                    likeService.getLikeCount(post2), likeService.getLikeCount(post1)
            ));
        } else {
            posts.sort(Comparator.comparing(Post::getCreatedAt).reversed());
        }

        return posts.stream().limit(100).collect(Collectors.toList());
    }

    @Override
    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    @Override
    @Transactional
    public Post updatePost(Long postId, String title, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: "));
        post.setTitle(title);
        post.setContent(content);
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // If the post has an associated image, delete it from the filesystem
        if (post.getImageUrl() != null) {
            deleteImageFile(post.getImageUrl());
        }
        postRepository.deleteById(postId);
    }

    @Override
    public String storeImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }

        try {
            String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path destinationPath = imageStoragePath.resolve(filename).normalize();

            Files.createDirectories(destinationPath.getParent()); // 경로가 없을 경우 생성
            Files.write(destinationPath, image.getBytes());

            return "/uploads/images/" + filename; // URL 반환
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image file.", e);
        }
    }

    private void deleteImageFile(String imageUrl) {
        try {
            String fileName = imageUrl.replace("/uploads/images/", "");
            Path imagePath = imageStoragePath.resolve(fileName).normalize();

            Files.deleteIfExists(imagePath); // 파일 삭제
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image file: " + imageUrl, e);
        }
    }
}