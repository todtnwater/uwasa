package com.uwasa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "UWASA_INQUIRIES")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Inquiries {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inquiries_seq")
    @SequenceGenerator(name = "inquiries_seq", sequenceName = "UWASA_INQUIRIES_SEQ", allocationSize = 1)
    @Column(name = "INQUIRY_ID")
    private Long inquiryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "inquiries", "password", "createdAt", "satisfaction", "points", "role", "username"})
    private Users user;

    @Column(name = "IP", nullable = false, length = 50)
    private String ip;

    @Column(name = "NICKNAME", nullable = false, length = 50)
    private String nickname;

    @Column(name = "TITLE", nullable = false, length = 100)
    private String title;

    @Column(name = "CONTENT", nullable = false, length = 2000)
    private String content;

    @Column(name = "IS_ANSWERED")
    private Integer isAnswered;

    @Column(name = "ANSWER", length = 2000)
    private String answer;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "ANSWERED_AT")
    private LocalDateTime answeredAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.isAnswered == null) this.isAnswered = 0;
    }

    public Long getInquiryId() { return inquiryId; }
    public void setInquiryId(Long inquiryId) { this.inquiryId = inquiryId; }
    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getIsAnswered() { return isAnswered; }
    public void setIsAnswered(Integer isAnswered) { this.isAnswered = isAnswered; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }
}


