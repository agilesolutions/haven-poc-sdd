package com.example.serviceb.service;

import com.example.serviceb.model.InfoEntity;
import com.example.serviceb.repository.InfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class InfoService {

    private final InfoRepository repo;

    public InfoService(InfoRepository repo) {
        this.repo = repo;
    }

    public Optional<InfoEntity> getInfo(String name) {
        return repo.findById(name);
    }

    @Transactional
    public InfoEntity upsert(InfoEntity entity) {
        entity.setUpdatedAt(OffsetDateTime.now());
        return repo.save(entity);
    }
}
