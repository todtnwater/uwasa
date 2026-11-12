package com.uwasa.service;

import com.uwasa.entity.BannedIps;
import com.uwasa.repository.BannedIpsRepository;
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
public class BannedIpsService {

    @Autowired
    private BannedIpsRepository bannedIpsRepository;

    @Transactional
    public void ban(String ip, String reason, LocalDateTime expiredAt) {
        BannedIps bannedIp = new BannedIps();
        bannedIp.setIp(ip);
        bannedIp.setReason(reason);
        bannedIp.setExpiredAt(expiredAt);
        bannedIpsRepository.save(bannedIp);
    }

    @Transactional(readOnly = true)
    public boolean isBanned(String ip) {
        return bannedIpsRepository.isBanned(ip, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<BannedIps> findAll() {
        return bannedIpsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<BannedIps> findAllPaged(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return bannedIpsRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional
    public void unban(Long banId) {
        bannedIpsRepository.deleteById(banId);
    }
}