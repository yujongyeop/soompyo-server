package com.soompyo.server.user.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.soompyo.server.user.domain.User;

@Repository
public interface UserRepository {

    User join(User user);

    Optional<User> findByEmail(String email);

    void deleteAll();

    Optional<User> findActiveByEmail(String email);

}
