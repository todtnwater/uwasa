package com.uwasa.repository;

import com.uwasa.entity.Reactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReactionsRepository extends JpaRepository<Reactions, Long> {

    List<Reactions> findByPostPostId(Long postId);

    boolean existsByPostPostIdAndIp(Long postId, String ip);

    Page<Reactions> findByEmojiOrderByCreatedAtDesc(String emoji, Pageable pageable);
}