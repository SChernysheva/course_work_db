package org.example.sport_section.Services.ImageService;

import org.example.sport_section.Models.Images.CourtImage;
import org.example.sport_section.Models.Images.Image;
import org.example.sport_section.Repositories.Images.ImageCourtRepository;
import org.example.sport_section.Repositories.Images.ImageRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final ImageCourtRepository imageCourtRepository;
    public ImageService(ImageRepository imageRepository, ImageCourtRepository imageCourtRepository) {
        this.imageRepository = imageRepository;
        this.imageCourtRepository = imageCourtRepository;
    }

    @Async
    public CompletableFuture<Image> getImageByPage(String page) {
        return CompletableFuture.supplyAsync(() -> imageRepository.findByImagePage(page));
    }

    @Async
    public CompletableFuture<CourtImage> getImageByCourtId(Integer id) {
        return CompletableFuture.supplyAsync(() -> imageCourtRepository.findByCourtId(id));
    }

}
