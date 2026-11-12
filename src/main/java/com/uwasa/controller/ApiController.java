package com.uwasa.controller;

import com.uwasa.entity.BannedIps;
import com.uwasa.entity.Inquiries;
import com.uwasa.entity.Posts;
import com.uwasa.entity.Reactions;
import com.uwasa.entity.Users;
import com.uwasa.repository.BannedIpsRepository;
import com.uwasa.repository.InquiriesRepository;
import com.uwasa.repository.PostsRepository;
import com.uwasa.repository.ReactionsRepository;
import com.uwasa.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private InquiriesRepository inquiriesRepository;

    @Autowired
    private BannedIpsRepository bannedIpsRepository;

    @Autowired
    private ReactionsRepository reactionsRepository;

    @Autowired
    private UsersService usersService;

    // 메인 글 목록
    @GetMapping("/posts")
    public List<Posts> getPosts() {
        return postsRepository.findByIsConsumedOrderByCreatedAtAsc(0);
    }

    // 내 글 목록 (무한스크롤)
    @GetMapping("/myposts")
    public Map<String, Object> getMyPosts(@RequestParam(defaultValue = "0") int page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            result.put("content", List.of());
            result.put("hasNext", false);
            return result;
        }

        Users user = usersService.findByUsername(auth.getName());
        if (user == null) {
            result.put("content", List.of());
            result.put("hasNext", false);
            return result;
        }

        Pageable pageable = PageRequest.of(page, 10);
        Page<Posts> pageResult = postsRepository.findByUserUserIdOrderByCreatedAtDesc(user.getUserId(), pageable);

        result.put("content", pageResult.getContent());
        result.put("hasNext", pageResult.hasNext());
        return result;
    }

    // 내 문의 목록 (무한스크롤)
    @GetMapping("/inquiries")
    public Map<String, Object> getMyInquiries(@RequestParam(defaultValue = "0") int page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            result.put("content", List.of());
            result.put("hasNext", false);
            return result;
        }

        Users user = usersService.findByUsername(auth.getName());
        if (user == null) {
            result.put("content", List.of());
            result.put("hasNext", false);
            return result;
        }

        Pageable pageable = PageRequest.of(page, 10);
        Page<Inquiries> pageResult = inquiriesRepository.findByUserUserIdOrderByCreatedAtDesc(user.getUserId(), pageable);

        result.put("content", pageResult.getContent());
        result.put("hasNext", pageResult.hasNext());
        return result;
    }

    // 관리자 문의 목록 (무한스크롤)
    @GetMapping("/admin/inquiries")
    public Map<String, Object> getAdminInquiries(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Inquiries> pageResult = inquiriesRepository.findAllByOrderByCreatedAtDesc(pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("content", pageResult.getContent());
        result.put("hasNext", pageResult.hasNext());
        return result;
    }

    // 관리자 밴 목록 (무한스크롤)
    @GetMapping("/admin/bans")
    public Map<String, Object> getAdminBans(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<BannedIps> pageResult = bannedIpsRepository.findAllByOrderByCreatedAtDesc(pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("content", pageResult.getContent());
        result.put("hasNext", pageResult.hasNext());
        return result;
    }

    // 신고된 반응 목록 (무한스크롤)
    @GetMapping("/admin/reports")
    public Map<String, Object> getReports(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Reactions> pageResult = reactionsRepository.findByEmojiOrderByCreatedAtDesc("🚨", pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("content", pageResult.getContent());
        result.put("hasNext", pageResult.hasNext());
        return result;
    }
}