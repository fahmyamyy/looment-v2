package com.looment.coreservice.repositories;

import com.looment.coreservice.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByUsernameEqualsIgnoreCase(String username);
    Page<Users> findByUsernameContainsIgnoreCase(Pageable pageable, String username);
}
