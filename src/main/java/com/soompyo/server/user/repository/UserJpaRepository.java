package com.soompyo.server.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soompyo.server.user.domain.User;
import com.soompyo.server.user.domain.UserStatus;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, UserStatus userStatus);

}
