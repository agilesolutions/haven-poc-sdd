package com.example.serviceb.controller;

import com.example.serviceb.model.InfoEntity;
import com.example.serviceb.service.InfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private final InfoService service;

    public InfoController(InfoService service) {
        this.service = service;
    }

    @GetMapping("/info")
    public ResponseEntity<InfoEntity> getInfo() {
        return service.getInfo("default")
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/info")
    public ResponseEntity<InfoEntity> putInfo(@RequestBody InfoEntity payload) {
        if (payload.getName() == null || payload.getName().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        InfoEntity saved = service.upsert(payload);
        return ResponseEntity.ok(saved);
    }
}
