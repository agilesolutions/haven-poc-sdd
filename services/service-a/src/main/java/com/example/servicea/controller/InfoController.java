package com.example.servicea.controller;

import com.example.servicea.client.InfoClient;
import com.example.servicea.dto.InfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private final InfoClient infoClient;

    public InfoController(InfoClient infoClient) {
        this.infoClient = infoClient;
    }

    @GetMapping("/info")
    public ResponseEntity<InfoDto> info() {
        InfoDto dto = infoClient.getInfo();
        if (dto == null) return ResponseEntity.status(502).build();
        return ResponseEntity.ok(dto);
    }
}
