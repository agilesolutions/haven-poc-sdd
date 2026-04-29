package com.example.serviceb.repository;

import com.example.serviceb.model.InfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoRepository extends JpaRepository<InfoEntity, String> {
}
