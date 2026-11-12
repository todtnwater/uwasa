package com.uwasa.service;

import com.uwasa.entity.Inquiries;
import com.uwasa.entity.Users;
import com.uwasa.repository.InquiriesRepository;
import com.uwasa.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InquiriesService {

    @Autowired
    private InquiriesRepository inquiriesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Transactional
    public void write(String title, String content, String ip, String nickname, Long userId) {
        Inquiries inquiry = new Inquiries();
        inquiry.setTitle(title);
        inquiry.setContent(content);
        inquiry.setIp(ip);
        inquiry.setNickname(nickname);
        inquiry.setIsAnswered(0);

        if (userId != null) {
            Users user = usersRepository.findById(userId).orElse(null);
            inquiry.setUser(user);
        }

        inquiriesRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public List<Inquiries> findAll() {
        return inquiriesRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Page<Inquiries> findAllPaged(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return inquiriesRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public List<Inquiries> findMyInquiries(Long userId) {
        return inquiriesRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Page<Inquiries> findMyInquiriesPaged(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return inquiriesRepository.findByUserUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Inquiries findById(Long inquiryId) {
        return inquiriesRepository.findById(inquiryId).orElse(null);
    }

    @Transactional
    public void answer(Long inquiryId, String answer) {
        Inquiries inquiry = inquiriesRepository.findById(inquiryId).orElse(null);
        if (inquiry != null) {
            inquiry.setAnswer(answer);
            inquiry.setIsAnswered(1);
            inquiry.setAnsweredAt(LocalDateTime.now());
            inquiriesRepository.save(inquiry);
        }
    }
}