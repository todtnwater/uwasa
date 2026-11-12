package com.uwasa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "UWASA_POSTS")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_seq")
    @SequenceGenerator(name = "posts_seq", sequenceName = "UWASA_POSTS_SEQ", allocationSize = 1)
    @Column(name = "POST_ID")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "posts", "password", "createdAt", "satisfaction", "points", "role", "username"})
    private Users user;

    @Column(name = "TITLE", nullable = false, length = 100)
    private String title;

    @Column(name = "CONTENT", nullable = false, length = 4000)
    private String content;

    @Column(name = "IP", nullable = false, length = 50)
    private String ip;

    @Column(name = "READER_IP", length = 50)
    private String readerIp;

    @Column(name = "IS_CONSUMED")
    private Integer isConsumed;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CONSUMED_AT")
    private LocalDateTime consumedAt;

    @Column(name = "WRITER_NICKNAME", length = 50)
    private String writerNickname;

    @Column(name = "READER_NICKNAME", length = 50)
    private String readerNickname;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.isConsumed == null) this.isConsumed = 0;
    }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getReaderIp() { return readerIp; }
    public void setReaderIp(String readerIp) { this.readerIp = readerIp; }
    public Integer getIsConsumed() { return isConsumed; }
    public void setIsConsumed(Integer isConsumed) { this.isConsumed = isConsumed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getConsumedAt() { return consumedAt; }
    public void setConsumedAt(LocalDateTime consumedAt) { this.consumedAt = consumedAt; }
    public String getWriterNickname() { return writerNickname; }
    public void setWriterNickname(String writerNickname) { this.writerNickname = writerNickname; }
    public String getReaderNickname() { return readerNickname; }
    public void setReaderNickname(String readerNickname) { this.readerNickname = readerNickname; }
}