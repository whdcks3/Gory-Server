package com.whdcks3.portfolio.gory_server.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whdcks3.portfolio.gory_server.data.models.user.EmailVerification;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import java.util.List;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByToken(String token);
}
