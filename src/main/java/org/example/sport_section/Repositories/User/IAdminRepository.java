package org.example.sport_section.Repositories.User;

import org.example.sport_section.Models.Users.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IAdminRepository extends JpaRepository<Admin, Integer> {
//    @Query(value = "SELECT * FROM admins WHERE user_id = :userId", nativeQuery = true)
//    public Admin getAdmin(@Param("userId") int userId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO admins (user_id) VALUES (:userId)", nativeQuery = true)
    public Integer save(@Param("userId") int userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Admin a WHERE a.id = :id")
    void deleteById(@Param("id") int id);

}
