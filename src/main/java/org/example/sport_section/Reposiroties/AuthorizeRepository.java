package org.example.sport_section.Reposiroties;

import org.example.sport_section.DataBase.DatabaseConfig;
import org.example.sport_section.Models.Court;
import org.example.sport_section.Models.UserModelAuthorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class AuthorizeRepository {
    private final DatabaseConfig databaseConfig;

    @Autowired
    public AuthorizeRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }
    public boolean isEmailInDatabase(String email) {
        String sql = "SELECT * FROM passwords WHERE email = ?";
        try (PreparedStatement preparedStatement = databaseConfig.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public String getHashPasswordForEmail(String email) {
        String sql = "SELECT * FROM passwords WHERE email = ?";
        String hash = null;
        try (PreparedStatement preparedStatement = databaseConfig.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next()) {
                hash = res.getString("hash_password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hash;
    }
    public UserModelAuthorization getUser(String email) {
        String sql = "SELECT * FROM passwords WHERE email = ?";
        UserModelAuthorization user = null;
        try (PreparedStatement preparedStatement = databaseConfig.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next()) {
                user = new UserModelAuthorization();
                user.setEmail(email);
                user.setHashPassword(res.getString("hash_password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    public void saveEmail(String email, String password, long userId) {
        String sql = "INSERT INTO passwords (email, hash_password, id) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = databaseConfig.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            preparedStatement.setString(2, hashPassword);
            preparedStatement.setLong(3, userId);
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
