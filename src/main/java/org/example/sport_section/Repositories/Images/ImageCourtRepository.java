package org.example.sport_section.Repositories.Images;

import org.example.sport_section.Models.Images.CourtImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageCourtRepository extends JpaRepository<CourtImage, Integer> {
    @Query(value = "SELECT * FROM court_images WHERE court_id = :id", nativeQuery = true)
    public Optional<CourtImage> findByCourtId(@Param("id") Integer id);
}
