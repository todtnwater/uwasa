package com.uwasa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "UWASA_BANNED_IPS")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BannedIps {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "banned_ips_seq")
    @SequenceGenerator(name = "banned_ips_seq", sequenceName = "UWASA_BANNED_IPS_SEQ", allocationSize = 1)
    @Column(name = "BAN_ID")
    private Long banId;

    @Column(name = "IP", nullable = false, length = 50)
    private String ip;

    @Column(name = "REASON", length = 500)
    private String reason;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "EXPIRED_AT")
    private LocalDateTime expiredAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getBanId() { return banId; }
    public void setBanId(Long banId) { this.banId = banId; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public void setExpiredAt(LocalDateTime expiredAt) { this.expiredAt = expiredAt; }
}