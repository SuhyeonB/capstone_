package com.engdiarytoon.server.like;

import com.engdiarytoon.server.post.Post;
import com.engdiarytoon.server.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserAndPost(User user, Post post);

    int countByPost(Post post);

    void deleteByUserAndPost(User user, Post post);
}
