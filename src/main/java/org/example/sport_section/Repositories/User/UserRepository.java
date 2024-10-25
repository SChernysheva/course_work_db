package org.example.sport_section.Repositories.User;

import org.example.sport_section.DataBase.DatabaseConfig;
import org.example.sport_section.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserRepository {
    private final DatabaseConfig databaseConfig;

    @Autowired
    public UserRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public List<User> getUsers() {
        List<User> people = new ArrayList<>();
        try
        {
            Statement statement = databaseConfig.getConnection().createStatement();
            String SQL = "SELECT * FROM users";
            ResultSet res = statement.executeQuery(SQL);
            while (res.next())
            {
                User user = new User();
                user.setId(res.getInt("id"));
                user.setFirst_name(res.getString("first_name"));
                user.setLast_name(res.getString("last_name"));
                user.setEmail(res.getString("email"));
                user.setPhone(res.getString("phone"));
                people.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //todo
        }
        return people;
    }

    public User getUser(long id) {
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = databaseConfig.getConnection().prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next()) {
                user = new User();
                user.setId(res.getInt("id"));
                user.setFirst_name(res.getString("first_name"));
                user.setLast_name(res.getString("last_name"));
                user.setEmail(res.getString("email"));
                user.setPhone(res.getString("phone"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //todo
        }
        return user;
    }



    public Long addUser(User user)
    {
        try
        {
            String sql = "INSERT INTO users (first_name, last_name, email, phone) values (?, ?, ?, ?)";
            PreparedStatement statement = databaseConfig.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, user.getFirst_name());
            statement.setString(2, user.getLast_name());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPhone());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    }
                }
            } else {
                throw new SQLException("No rows affected.");
                //todo
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //todo
        }
        return null;
    }

    public void deleteUser(long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = databaseConfig.getConnection().prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //todo update
}
