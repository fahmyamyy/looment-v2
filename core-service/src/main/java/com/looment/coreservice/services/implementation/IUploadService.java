package com.looment.coreservice.services.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IUploadService {
    List<String> getUrlParent(UUID parentId);
    void deleteByParent(UUID parentId, LocalDateTime now);
}
