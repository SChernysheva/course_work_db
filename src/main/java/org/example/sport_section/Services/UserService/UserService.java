package org.example.sport_section.Services.UserService;


import org.example.sport_section.DataBase.DatabaseConfig;
import org.example.sport_section.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserService {
    @Autowired
    DatabaseConfig databaseConfig;

    public List<User> getUsers() throws SQLException {
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
                user.setPatronymic(res.getString("patronymic"));
                user.setEmail(res.getString("email"));
                user.setPhone(res.getString("phone"));
                people.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return people;
    }

    public User getUser(int id) {
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = databaseConfig.getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next()) {
                user = new User();
                user.setId(res.getInt("id"));
                user.setFirst_name(res.getString("first_name"));
                user.setLast_name(res.getString("last_name"));
                user.setPatronymic(res.getString("patronymic"));
                user.setEmail(res.getString("email"));
                user.setPhone(res.getString("phone"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }



    public void addUser(User user) throws SQLException
    {
        try
        {
            PreparedStatement statement = databaseConfig.getConnection().prepareStatement(
                    "INSERT INTO users (first_name, last_name, patronyic, email, phome) values (?, ?, ?, ?, ?)"
            );
            statement.setString(1, user.getFirst_name());
            statement.setString(2, user.getLast_name());
            statement.setString(3, user.getPatronymic());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPhone());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = databaseConfig.getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //todo update
}
