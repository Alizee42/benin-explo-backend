package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.MediaDTO;
import com.beninexplo.backend.entity.Media;
import com.beninexplo.backend.repository.MediaRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaService {

    private final MediaRepository repo;

    public MediaService(MediaRepository repo) {
        this.repo = repo;
    }

    private MediaDTO toDTO(Media m) {
        return new MediaDTO(
                m.getIdMedia(),
                m.getUrl(),
                m.getType(),
                m.getDescription()
        );
    }

    private Media fromDTO(MediaDTO dto) {
        Media m = new Media();

        m.setIdMedia(dto.getId());
        m.setUrl(dto.getUrl());
        m.setType(dto.getType());
        m.setDescription(dto.getDescription());

        return m;
    }

    public List<MediaDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MediaDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public MediaDTO create(MediaDTO dto) {
        Media saved = repo.save(fromDTO(dto));
        return toDTO(saved);
    }

    public MediaDTO update(Long id, MediaDTO dto) {
        Media existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Media non trouvé"));

        existing.setUrl(dto.getUrl());
        existing.setType(dto.getType());
        existing.setDescription(dto.getDescription());

        return toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
