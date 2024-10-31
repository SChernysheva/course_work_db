package org.example.sport_section.Repositories.Courts;

import org.example.sport_section.Models.Courts.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICourtRepository extends JpaRepository<Court, Integer> {

}
