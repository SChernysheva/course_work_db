package org.example.sport_section.Services.ImageService;

import org.example.sport_section.Models.Images.Image;
import org.example.sport_section.Repositories.Images.ImageRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Async
    public CompletableFuture<Optional<Image>> getImageByPage(String page) {
        return CompletableFuture.supplyAsync(() -> imageRepository.findByImagePage(page));
    }


}
