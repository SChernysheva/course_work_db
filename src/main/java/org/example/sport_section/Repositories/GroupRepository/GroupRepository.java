package org.example.sport_section.Repositories.GroupRepository;

import org.example.sport_section.Models.Groups.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    @Procedure(value = "update_coach_in_group")
    void editCoach(int groupId, int coachId);
}
