package com.uwasa.repository;

import com.uwasa.entity.Inquiries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InquiriesRepository extends JpaRepository<Inquiries, Long> {

    List<Inquiries> findAllByOrderByCreatedAtDesc();

    Page<Inquiries> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Inquiries> findByUserUserIdOrderByCreatedAtDesc(Long userId);

    Page<Inquiries> findByUserUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}