package com.soompyo.server.user.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.soompyo.server.user.domain.User;
import com.soompyo.server.user.domain.UserStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User join(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public void deleteAll() {
        userJpaRepository.deleteAll();
    }

    @Override
    public Optional<User> findActiveByEmail(String email) {
        return userJpaRepository.findByEmailAndStatus(email, UserStatus.ACTIVE);
    }

}
