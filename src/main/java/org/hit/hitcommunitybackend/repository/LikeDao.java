package org.hit.hitcommunitybackend.repository;

import org.hibernate.mapping.List;
import org.hit.hitcommunitybackend.domain.Like;
import org.hit.hitcommunitybackend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeDao extends JpaRepository<Like, Integer> {
    Like save(Like like);
    Optional<Like> findById(Integer lid);
    void deleteById(Integer lid);
    // 根据帖子ID查找所有的点赞记录
    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.pid = :postId")
    Integer findByPostId(Integer postId);
    
}