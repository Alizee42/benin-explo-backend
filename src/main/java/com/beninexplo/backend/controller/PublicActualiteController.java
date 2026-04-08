package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ActualiteDTO;
import com.beninexplo.backend.service.ActualiteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/actualites")
public class PublicActualiteController {

    private final ActualiteService actualiteService;

    public PublicActualiteController(ActualiteService actualiteService) {
        this.actualiteService = actualiteService;
    }

    @GetMapping
    public List<ActualiteDTO> getPublished() {
        return actualiteService.getPublished();
    }

    @GetMapping("/{id}")
    public ActualiteDTO getPublishedById(@PathVariable Long id) {
        return actualiteService.getPublished(id);
    }
}
