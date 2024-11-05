package org.example.sport_section.Repositories.CoachRepository;

import org.example.sport_section.Models.Users.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Integer> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO coaches (user_id) VALUES (:userId)", nativeQuery = true)
    public Integer save(@Param("userId") int userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Coach c WHERE c.id = :id")
    void deleteById(@Param("id") int id);
}
