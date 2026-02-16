package com.pipeline.service;

import java.util.Optional;

public interface S3StorageService {
    /**
     * Uploads given content to the configured bucket under a generated or provided key.
     * Returns the S3 key (object key) used.
     */
    String upload(String key, String content, String contentType);

    Optional<String> get(String key);

    void delete(String key);
}
