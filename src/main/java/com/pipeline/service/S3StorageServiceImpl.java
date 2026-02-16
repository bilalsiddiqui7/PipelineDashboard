package com.pipeline.service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class S3StorageServiceImpl implements S3StorageService {

    private final S3Client s3;
    @Value("${aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(String key, String content, String contentType) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3.putObject(req, RequestBody.fromString(content));
        return key;
    }

    @Override
    public Optional<String> get(String key) {
        try {
            GetObjectRequest req = GetObjectRequest.builder().bucket(bucket).key(key).build();
            try (InputStream is = s3.getObject(req)) {
                return Optional.of(new String(is.readAllBytes(), StandardCharsets.UTF_8));
            }
        } catch (S3Exception | IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(String key) {
        try {
            DeleteObjectRequest r = DeleteObjectRequest.builder().bucket(bucket).key(key).build();
            s3.deleteObject(r);
        } catch (S3Exception e) {

        }
    }
}
