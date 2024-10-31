package org.example.sport_section.Repositories.Images;

import org.example.sport_section.Models.Images.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    @Query(value = "SELECT * FROM ui_images WHERE image_page = :page", nativeQuery = true)
    public Optional<Image> findByImagePage(@Param("page") String page);
}
