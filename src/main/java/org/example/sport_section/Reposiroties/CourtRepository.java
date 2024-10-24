package org.example.sport_section.Reposiroties;

import org.example.sport_section.DataBase.DatabaseConfig;
import org.example.sport_section.Models.Court;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class CourtRepository {
    private final DatabaseConfig databaseConfig;

    @Autowired
    public CourtRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public List<Court> getCourts() throws SQLException {
        List<Court> courts = new ArrayList<>();
        try
        {
            Statement statement = databaseConfig.getConnection().createStatement();
            String SQL = "SELECT * FROM courts";
            ResultSet res = statement.executeQuery(SQL);
            while (res.next())
            {
                Court court = new Court();
                court.setId(res.getInt("id"));
                court.setNameCourt(res.getString("court_name"));
                courts.add(court);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courts;
    }
}
