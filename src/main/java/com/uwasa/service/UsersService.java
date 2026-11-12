package com.uwasa.service;

import com.uwasa.entity.Users;
import com.uwasa.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public boolean join(String username, String password, String nickname) {
        if (usersRepository.existsByUsername(username)) {
            return false;
        }
        Users user = new Users();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setRole("USER");
        user.setSatisfaction(0L);
        user.setPoints(0L);
        usersRepository.save(user);
        return true;
    }

    // username으로 조회
    @Transactional(readOnly = true)
    public Users findByUsername(String username) {
        return usersRepository.findByUsername(username).orElse(null);
    }

    // userId로 조회
    @Transactional(readOnly = true)
    public Users findById(Long userId) {
        return usersRepository.findById(userId).orElse(null);
    }

    // username 중복 체크
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return usersRepository.existsByUsername(username);
    }

    // 포인트 차감
    @Transactional
    public void deductPoints(Long userId, Long amount) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setPoints(user.getPoints() - amount);
            usersRepository.save(user);
        }
    }

    // 포인트 적립
    @Transactional
    public void addPoints(Long userId, Long amount) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setPoints(user.getPoints() + amount);
            usersRepository.save(user);
        }
    }

    // 만족도 변경
    @Transactional
    public void updateSatisfaction(Long userId, Long amount) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setSatisfaction(user.getSatisfaction() + amount);
            usersRepository.save(user);
        }
    }
}