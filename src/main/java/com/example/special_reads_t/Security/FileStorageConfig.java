package com.example.special_reads_t.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Value("${upload.dir}")
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }
}