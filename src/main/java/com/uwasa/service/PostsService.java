package com.uwasa.service;

import com.uwasa.controller.SseController;
import com.uwasa.entity.Posts;
import com.uwasa.entity.Users;
import com.uwasa.repository.PostsRepository;
import com.uwasa.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PostsService {

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private UsersRepository usersRepository;

    private static final Map<String, LocalDateTime> lastWriteTime = new ConcurrentHashMap<>();

    public enum WriteResult {
        SUCCESS, BAD_WORD, COOLDOWN
    }

    private static final String[] BAD_WORDS = {
        "시발", "tlqkf", "씨발", "앰창", "엠창", "개노맛",
        "새끼", "시1발", "새1끼", "씨1발", "ㅅ1발", "ㅆ1발",
        "십새기", "개새기", "씹새기", "씹", "병신", "존나",
        "니엄마", "니 엄마", "애미", "니앰", "느그", "느금",
        "애비", "니아빠", "18년", "18놈", "18새끼", "갈보",
        "강간", "개가튼", "개같은", "개걸레", "개년", "개놈",
        "개보지", "개새", "개씨", "개자식", "개좆", "개지랄",
        "걸레", "걸레년", "귀두", "년", "니기미",
        "대딸", "ㄷㄸ", "우흥", "부엉이바위", "깨비", "ㄴㅁㅎ",
        "노무", "무현", "노무현", "고무통", "니애미", "닌기미",
        "니미", "따먹", "딸딸이", "띠발뇬", "띠팔", "미친",
        "미친넘", "미친년", "미친놈", "미친새끼", "버지", "벌창",
        "보지", "부랄", "불알", "뷩딱", "븅신", "빠구리",
        "뻑큐", "ㅅㅂ", "사까시", "색스", "샤발", "성교",
        "성폭행", "섹스", "쉬발", "쉬팔", "시발년", "시발놈",
        "시발새끼", "시팔", "십새", "십창", "십팔새끼", "ㅆㅂ",
        "쌍년", "쌍놈", "씨발년", "씨부랄", "씨팔", "씹년",
        "씹새", "씹창", "아가리", "애무", "애자", "양아치",
        "염병", "엿먹어라", "오랄", "유두", "유방", "육갑",
        "자지", "잠지", "잡년", "잡놈", "정액", "젖까",
        "조까", "졸라", "좃", "좆", "좆까", "좆나",
        "좆도", "좆새끼", "지랄", "창녀", "창년", "처먹",
        "쳐먹", "페니스", "핥", "호로", "호모", "후장",
        "마약", "필로폰", "히로뽕", "뽕", "대마", "대마초",
        "코카인", "헤로인", "마리화나", "엑스터시", "몰핀", "아편",
        "LSD", "케타민", "펜타닐", "야바", "빙두", "떨",
        "총알", "백색가루", "지펠", "ㅈㄹ", "ㅂㅅ", "ㅅㄲ",
        "ㄱㅅㄲ", "ㅁㅊ", "ㅈㄴ", "ㄷㅊ", "틀딱", "노인충",
        "한남", "한녀", "맘충", "급식충", "진지충", "좌빨",
        "우빨", "홍어", "전라디언", "경상도놈", "흑형", "짱깨",
        "쪽바리", "왜놈", "짱꼴라", "야동", "야설", "19금",
        "몸캠", "조건만남", "원나잇", "헌팅", "성인방", "텔레방",
        "오피", "안마방", "룸살롱", "ㅈㄱ", "ㅇㅍ", "ㅁㄴ",
        "ㅍㅇ", "신상털기", "신상털", "신상", "ㄷㅇ", "도용",
        "사칭", "조건", "조건녀", "조건남", "스폰", "스폰서",
        "스폰받음", "몸팔", "몸사", "몸구함", "출장", "출장마사지",
        "출장안마", "텔레", "텔레그램", "입금", "계좌", "송금",
        "선입금", "사기", "먹튀", "성인", "성인용", "야한",
        "낚시", "미끼", "클릭", "광고", "홍보", "이벤트당첨",
        "바이럴", "뒷광고", "해킹", "피싱", "사이트", "무료나눔", "공짜", "선물드림", "바카라",
        "ㅂㅋㄹ", "카지노", "ㅋㅈㄴ", "슬롯머신", "슬롯", "포커",
        "블랙잭", "홀덤", "섯다", "화투", "맞고", "토토",
        "스포츠토토", "불법도박", "온라인도박", "도박사이트", "배팅", "베팅",
        "환전", "충전", "파워볼", "사다리", "달팽이", "로하이",
        "다리다리", "먹튀사이트", "검증사이트", "배당", "베팅방", "운지",
        "mc무현", "노알라", "세월호어묵", "통구이", "통구이드립",
        "홍어드립", "홍어즙", "경상디언", "보슬아치", "김치녀",
        "한남충", "암베충", "로린이", "냄비", "일베충", "좌좀",
        "우좀", "종북", "빨갱이", "토왜", "토착왜구", "찐따",
        "씹덕", "패드립", "고인드립", "시체드립", "시체팔이", "bitch",
        "bastard", "fuck", "f uck", "f*ck", "fuk", "fucker",
        "fucking", "fuckyou", "fvck", "f**k", "asshole", "ass",
        "a**", "a55", "shit", "sh1t", "sh!t", "s**t",
        "cunt", "c**t", "dick", "d!ck", "d**k", "cock",
        "c**k", "pussy", "p**sy", "whore", "wh*re", "slut",
        "sl*t", "nigger", "nigga", "n*gger", "n*gga", "sex",
        "s*x", "porn", "p**n", "penis", "pen1s", "vagina",
        "motherfucker", "motherf**ker", "mf", "wtf", "kys",
    };

    private boolean containsBadWord(String text) {
        if (text == null) return false;
        String lower = text.toLowerCase();
        for (String word : BAD_WORDS) {
            if (lower.contains(word.toLowerCase())) return true;
        }
        return false;
    }

    @Transactional
    public WriteResult write(String title, String content, String ip, String writerNickname, Long userId) {
        if (containsBadWord(title) || containsBadWord(content)) return WriteResult.BAD_WORD;

        LocalDateTime last = lastWriteTime.get(ip);
        if (last != null && last.plusSeconds(10).isAfter(LocalDateTime.now())) {
            return WriteResult.COOLDOWN;
        }
        lastWriteTime.put(ip, LocalDateTime.now());

        Posts post = new Posts();
        post.setTitle(title);
        post.setContent(content);
        post.setIp(ip);
        post.setWriterNickname(writerNickname);
        post.setIsConsumed(0);

        if (userId != null) {
            Users user = usersRepository.findById(userId).orElse(null);
            post.setUser(user);
        }

        postsRepository.save(post);

        final Long postId = post.getPostId();
        final String postTitle = post.getTitle();
        final String nickname = post.getWriterNickname();
        final String createdAt = post.getCreatedAt().toString();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                SseController.sendCreated(postId, postTitle, nickname, createdAt);
                pushStats();
            }
        });

        return WriteResult.SUCCESS;
    }

    @Transactional(readOnly = true)
    public List<Posts> getAlivePosts() {
        return postsRepository.findByIsConsumedOrderByCreatedAtAsc(0);
    }

    @Transactional
    public Posts consume(Long postId, String readerIp, String readerNickname) {
        Posts post = postsRepository.findById(postId).orElse(null);

        if (post == null || post.getIsConsumed() == 1) return null;

        if (post.getIp().equals(readerIp)) return post;

        postsRepository.consume(postId, readerIp, readerNickname, LocalDateTime.now());

        if (post.getUser() != null) {
            Users user = usersRepository.findById(post.getUser().getUserId()).orElse(null);
            if (user != null) {
                user.setPoints(user.getPoints() + 1);
                usersRepository.save(user);
            }
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                SseController.sendConsumed(postId);
                pushStats();
            }
        });

        return post;
    }

    @Transactional(readOnly = true)
    public Long countConsumedToday() {
        return postsRepository.countConsumedToday(LocalDateTime.now().toLocalDate().atStartOfDay());
    }

    @Transactional(readOnly = true)
    public Long countAlive() {
        return postsRepository.countByIsConsumed(0);
    }

    @Transactional(readOnly = true)
    public Long countTotalConsumed() {
        return postsRepository.countByIsConsumed(1);
    }

    @Transactional(readOnly = true)
    public List<Posts> getMyPosts(Long userId) {
        return postsRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Page<Posts> getMyPostsPaged(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return postsRepository.findByUserUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional
    public String revealReader(Long postId, Long userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null || user.getPoints() < 10) return null;

        Posts post = postsRepository.findById(postId).orElse(null);
        if (post == null || post.getIsConsumed() == 0) return null;

        user.setPoints(user.getPoints() - 10);
        usersRepository.save(user);
        return post.getReaderNickname();
    }

    private void pushStats() {
        SseController.sendStats(
            postsRepository.countConsumedToday(LocalDateTime.now().toLocalDate().atStartOfDay()),
            postsRepository.countByIsConsumed(0),
            postsRepository.countByIsConsumed(1)
        );
    }
}