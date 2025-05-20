package com.hrplatform.hrplatform.repository;

import com.hrplatform.hrplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findUserByFirstName(String firstName);
}
