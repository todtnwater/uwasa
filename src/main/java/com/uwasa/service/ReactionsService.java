package com.uwasa.service;

import com.uwasa.entity.Posts;
import com.uwasa.entity.Reactions;
import com.uwasa.entity.Users;
import com.uwasa.repository.PostsRepository;
import com.uwasa.repository.ReactionsRepository;
import com.uwasa.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

@Service
public class ReactionsService {

    @Autowired
    private ReactionsRepository reactionsRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Transactional
    public boolean react(Long postId, String emoji, String ip, String reportReason) {
        if (reactionsRepository.existsByPostPostIdAndIp(postId, ip)) return false;

        Posts post = postsRepository.findById(postId).orElse(null);
        if (post == null) return false;

        Reactions reaction = new Reactions();
        reaction.setPost(post);
        reaction.setEmoji(emoji);
        reaction.setIp(ip);
        if (emoji.equals("🚨") && reportReason != null) {
            reaction.setReportReason(reportReason);
        }
        reactionsRepository.save(reaction);

        if (post.getUser() != null) {
            Users user = usersRepository.findById(post.getUser().getUserId()).orElse(null);
            if (user != null) {
                if (emoji.equals("👍")) {
                    user.setSatisfaction(user.getSatisfaction() + 1);
                } else if (emoji.equals("🚨")) {
                    user.setSatisfaction(user.getSatisfaction() - 1);
                }
                usersRepository.save(user);
            }
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Page<Reactions> findReportsPaged(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return reactionsRepository.findByEmojiOrderByCreatedAtDesc("🚨", pageable);
    }
}