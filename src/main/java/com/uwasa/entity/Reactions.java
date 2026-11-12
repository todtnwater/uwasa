package com.uwasa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "UWASA_REACTIONS")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Reactions {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reactions_seq")
    @SequenceGenerator(name = "reactions_seq", sequenceName = "UWASA_REACTIONS_SEQ", allocationSize = 1)
    @Column(name = "REACTION_ID")
    private Long reactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Posts post;

    @Column(name = "EMOJI", nullable = false, length = 10)
    private String emoji;

    @Column(name = "IP", nullable = false, length = 50)
    private String ip;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    
    @Column(name = "REPORT_REASON", length = 500)
    private String reportReason;

    public String getReportReason() { return reportReason; }
    public void setReportReason(String reportReason) { this.reportReason = reportReason; }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getReactionId() { return reactionId; }
    public void setReactionId(Long reactionId) { this.reactionId = reactionId; }
    public Posts getPost() { return post; }
    public void setPost(Posts post) { this.post = post; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}