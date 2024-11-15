package org.example.sport_section.Repositories.Authorize;

import org.example.sport_section.Models.Users.UserModelAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component
public interface IAuthorizeRepository extends JpaRepository<UserModelAuthorization, Integer> {
    //here
    public Optional<UserModelAuthorization> getByEmail(String email);

    @Query(value = "SELECT * FROM get_hash_password_for_email(:email)", nativeQuery = true)
    public String getHashPasswordForEmail(@Param("email") String email);
}
