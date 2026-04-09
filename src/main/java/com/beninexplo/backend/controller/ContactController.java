package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ContactMessageDTO;
import com.beninexplo.backend.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> sendMessage(@Valid @RequestBody ContactMessageDTO dto) {
        contactService.sendContactMessage(dto);
        return ResponseEntity.ok(Map.of("message", "Votre message a bien ete recu. Nous vous repondrons sous 24h."));
    }
}
