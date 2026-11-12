package com.uwasa.repository;

import com.uwasa.entity.BannedIps;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface BannedIpsRepository extends JpaRepository<BannedIps, Long> {

    @Query("SELECT COUNT(b) > 0 FROM BannedIps b WHERE b.ip = :ip AND (b.expiredAt IS NULL OR b.expiredAt > :now)")
    boolean isBanned(@Param("ip") String ip, @Param("now") LocalDateTime now);

    Page<BannedIps> findAllByOrderByCreatedAtDesc(Pageable pageable);
}