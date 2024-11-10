package com.engdiarytoon.server.like;

import com.engdiarytoon.server.post.Post;
import com.engdiarytoon.server.user.User;

public interface LikeService {
    void likePost(User user, Post post);
    void unlikePost(User user, Post post);
    int getLikeCount(Post post);
    boolean hasUserLikedPost(User user, Post post);
}
