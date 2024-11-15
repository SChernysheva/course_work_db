package org.example.sport_section.Repositories.CoachRepository;

import org.example.sport_section.Models.Users.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Integer> {

    @Transactional
    @Procedure(value = "insert_into_coaches")
    Void insertIntoCoaches(int userId);

    @Transactional
    @Procedure(value = "delete_coach_by_id")
    Void deleteCoachById(int coach_id);
}
