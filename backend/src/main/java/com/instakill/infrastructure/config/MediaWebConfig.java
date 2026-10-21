package com.instakill.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class MediaWebConfig implements WebMvcConfigurer {

    @Value("${media.base-path:media}")
    private String mediaBasePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Convert relative path to absolute path to ensure file: scheme works perfectly
        String absoluteMediaPath = Paths.get(mediaBasePath).toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/media/**")
                .addResourceLocations(absoluteMediaPath);
    }
}