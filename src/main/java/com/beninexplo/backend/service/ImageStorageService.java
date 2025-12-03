package com.beninexplo.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    /**
     * Store the given file under configured storage location inside the provided folder.
     * Returns the stored filename (not the URL).
     */
    String store(MultipartFile file, String folder) throws Exception;
}
