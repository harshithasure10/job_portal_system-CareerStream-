package com.harshitha.jobportal.repository;

import com.harshitha.jobportal.entity.PasswordResetToken;
import com.harshitha.jobportal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    Optional<PasswordResetToken> findByToken(String token);

    @org.springframework.transaction.annotation.Transactional
    void deleteByUser(Users user);
}
