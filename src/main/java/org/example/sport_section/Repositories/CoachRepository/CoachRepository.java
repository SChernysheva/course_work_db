package org.example.sport_section.Repositories.CoachRepository;

import org.example.sport_section.Models.Users.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Integer> {

}
