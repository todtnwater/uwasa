package com.uwasa.repository;

import com.uwasa.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {

    List<Posts> findByIsConsumedOrderByCreatedAtAsc(Integer isConsumed);

    Page<Posts> findByUserUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Posts> findByUserUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COUNT(p) FROM Posts p WHERE p.isConsumed = 1 AND p.consumedAt >= :startOfDay")
    Long countConsumedToday(@Param("startOfDay") LocalDateTime startOfDay);

    Long countByIsConsumed(Integer isConsumed);

    @Modifying
    @Query("UPDATE Posts p SET p.isConsumed = 1, p.readerIp = :readerIp, p.readerNickname = :readerNickname, p.consumedAt = :consumedAt WHERE p.postId = :postId")
    void consume(@Param("postId") Long postId,
                 @Param("readerIp") String readerIp,
                 @Param("readerNickname") String readerNickname,
                 @Param("consumedAt") LocalDateTime consumedAt);
}