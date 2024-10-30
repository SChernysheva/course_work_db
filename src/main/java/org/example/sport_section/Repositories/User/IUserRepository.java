package org.example.sport_section.Repositories.User;

import org.example.sport_section.Models.Admin;
import org.example.sport_section.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    public User findByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "update users set group_id = :groupId where id = :userId", nativeQuery = true)
    public Integer addUserIntoGroup(@Param("groupId") Integer groupId, @Param("userId") int userId);


}
