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
        MediaDTO dto = new MediaDTO();
        dto.setId(m.getIdMedia());
        dto.setUrl(m.getUrl());
        dto.setType(m.getType());
        dto.setDescription(m.getDescription());
        dto.setDateUpload(m.getDateUpload().toString());
        return dto;
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
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MediaDTO get(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public MediaDTO create(MediaDTO dto) {
        Media m = fromDTO(dto);
        return toDTO(repo.save(m));
    }

    public MediaDTO update(Long id, MediaDTO dto) {
        dto.setId(id);
        return toDTO(repo.save(fromDTO(dto)));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
