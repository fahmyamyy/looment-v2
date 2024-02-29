package com.looment.coreservice.repositories;

import com.looment.coreservice.entities.Uploads;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UploadsRepository extends JpaRepository<Uploads, UUID> {
    List<Uploads> findByParentEquals(UUID parentId);
}
