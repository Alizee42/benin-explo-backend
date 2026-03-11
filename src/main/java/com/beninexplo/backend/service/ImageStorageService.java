package com.beninexplo.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    String store(MultipartFile file, String folder);
}
