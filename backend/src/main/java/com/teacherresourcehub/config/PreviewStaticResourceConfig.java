package com.teacherresourcehub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class PreviewStaticResourceConfig implements WebMvcConfigurer {

    private final String publicBaseUrl;
    private final Path storageRoot;

    public PreviewStaticResourceConfig(@Value("${app.preview.public-base-url:/preview-files}") String publicBaseUrl,
                                       @Value("${app.preview.storage-root:${user.home}/.teacher-resource-hub/preview-data}") String storageRoot) {
        this.publicBaseUrl = normalize(publicBaseUrl);
        this.storageRoot = Path.of(storageRoot);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(publicBaseUrl + "/**")
                .addResourceLocations(storageRoot.toUri().toString());
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "/preview-files";
        }
        String normalized = value.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        return normalized.endsWith("/") ? normalized.substring(0, normalized.length() - 1) : normalized;
    }
}
