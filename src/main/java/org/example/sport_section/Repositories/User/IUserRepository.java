package org.example.sport_section.Repositories.User;

import org.example.sport_section.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    public User findByEmail(@Param("email") String email);

}
