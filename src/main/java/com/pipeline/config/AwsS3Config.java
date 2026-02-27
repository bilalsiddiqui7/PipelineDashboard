package com.pipeline.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class AwsS3Config {

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    @Value("${aws.s3.endpoint:}")
    private String endpoint;

//        @Value("${aws.s3.path-style-access:false}")
//        private boolean pathStyle;

    // optional properties, TODO REMOVED - WE CAN ASSUME ENV VARS
    @Value("${aws.accessKeyId:}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey:}")
    private String secretAccessKey;

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder().region(Region.of(awsRegion));

        if (accessKeyId != null && !accessKeyId.isBlank() && secretAccessKey != null && !secretAccessKey.isBlank()) {
            AwsBasicCredentials creds = AwsBasicCredentials.create(accessKeyId.trim(), secretAccessKey.trim());
            builder.credentialsProvider(StaticCredentialsProvider.create(creds));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }

        String ep = endpoint == null ? "" : endpoint.trim();
        if (!ep.isBlank() && !ep.startsWith("#")) {
            try {
                URI uri = new URI(ep);
                builder.endpointOverride(uri);
            } catch (URISyntaxException | IllegalArgumentException e) {
                throw new IllegalStateException("Invalid aws.s3.endpoint value: " + ep, e);
            }
        }

        return builder.build();
    }
}