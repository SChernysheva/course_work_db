package org.example.sport_section.Repositories.User;

import org.example.sport_section.Models.Users.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IAdminRepository extends JpaRepository<Admin, Integer> {

    @Transactional
    @Procedure(value = "insert_into_admins")
    void insertIntoAdmins(int userId);

    @Transactional
    @Procedure(value = "delete_admin_by_id")
    void deleteAdminById(int adminId);

}
