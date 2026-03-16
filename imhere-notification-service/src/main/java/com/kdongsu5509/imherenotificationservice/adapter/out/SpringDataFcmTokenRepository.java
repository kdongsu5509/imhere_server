package com.kdongsu5509.imherenotificationservice.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataFcmTokenRepository extends JpaRepository<Long, FcmTokenJpaEntity> {
}
