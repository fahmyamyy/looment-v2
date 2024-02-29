package com.looment.coreservice.services;

import com.looment.coreservice.entities.Uploads;
import com.looment.coreservice.repositories.UploadsRepository;
import com.looment.coreservice.services.implementation.IUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadService implements IUploadService {
    private final UploadsRepository uploadsRepository;

    @Override
    public List<String> getUrlParent(UUID parentId) {
        return uploadsRepository.findByParentEquals(parentId).stream()
                .map(Uploads::getUrl).toList();
    }

    @Override
    public void deleteByParent(UUID parentId, LocalDateTime now) {
        List<Uploads> uploadsList = uploadsRepository.findByParentEquals(parentId);

        uploadsList.forEach(upload -> upload.setDeletedAt(now));
        uploadsRepository.saveAll(uploadsList);
    }
}
