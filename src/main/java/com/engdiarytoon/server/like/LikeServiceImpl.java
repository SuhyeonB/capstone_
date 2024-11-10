package com.engdiarytoon.server.like;

import com.engdiarytoon.server.post.Post;
import com.engdiarytoon.server.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeServiceImpl implements LikeService{

    private final LikeRepository likeRepository;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }
    @Override
    @Transactional
    public void likePost(User user, Post post) {
        if (!likeRepository.existsByUserAndPost(user, post)) {
            Like like = Like.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeRepository.save(like);
        } else {
            throw new IllegalStateException("already liked");
        }
    }

    @Override
    public void unlikePost(User user, Post post) {
        if (likeRepository.existsByUserAndPost(user, post)) {
            likeRepository.deleteByUserAndPost(user, post);
        } else {
            throw new IllegalStateException("User has not liked this post.");
        }
    }

    @Override
    public int getLikeCount(Post post) {
        return likeRepository.countByPost(post);
    }

    @Override
    public boolean hasUserLikedPost(User user, Post post) {
        return likeRepository.existsByUserAndPost(user, post);
    }
}
