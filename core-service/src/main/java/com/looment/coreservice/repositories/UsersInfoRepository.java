package com.looment.coreservice.repositories;

import com.looment.coreservice.entities.UsersInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsersInfoRepository extends JpaRepository<UsersInfo, UUID> {
}
