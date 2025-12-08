package com.ticketml.repository;


import com.ticketml.common.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGoogleId(String googleId);

    Optional<User> findByEmail(@NotNull(message = "Email is required") @Email(message = "Invalid email format") String email);
}
