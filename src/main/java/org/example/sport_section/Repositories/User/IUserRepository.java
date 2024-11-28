package org.example.sport_section.Repositories.User;

import org.example.sport_section.Models.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {
    @Query(value = "SELECT * FROM GET_USER_BY_EMAIL(:email)", nativeQuery = true)
    public Optional<User> findByEmail(String email);

    @Transactional
    @Procedure(value = "add_user_into_group")
    void addUserIntoGroup(int userId, Integer groupId);

}
