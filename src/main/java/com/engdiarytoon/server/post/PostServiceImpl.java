package com.engdiarytoon.server.post;

import com.engdiarytoon.server.user.User;
import com.engdiarytoon.server.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService{

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public PostServiceImpl(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }
    @Override
    public Post createPost(String title, String content, Long userId, Boolean isPublic, String weather) {
        User writer = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: "));

        Post post = Post.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .isPublic(isPublic)
                .weather(weather)
                .createdAt(LocalDateTime.now())
                .build();
        return postRepository.save(post);
    }

    @Override
    public List<Post> findByUser(Long userId) {
        return postRepository.findByWriterUserId(userId);
    }

    @Override
    public List<Post> findAllWithPublic() {
        return postRepository.findByIsPublicTrue();
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
        if(!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }
        postRepository.deleteById(postId);
    }
}
