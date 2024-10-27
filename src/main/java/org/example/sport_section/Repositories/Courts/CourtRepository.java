package org.example.sport_section.Repositories.Courts;

import org.example.sport_section.DataBase.DatabaseConfig;
import org.example.sport_section.Models.Court;
import org.example.sport_section.front.Views.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class CourtRepository {
    private final DatabaseConfig databaseConfig;

    @Autowired
    public CourtRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public List<Court> getCourts() throws SQLException {
        List<Court> courts = new ArrayList<>();
        try {
            Statement statement = databaseConfig.getConnection().createStatement();
            String SQL = "SELECT * FROM courts";
            ResultSet res = statement.executeQuery(SQL);
            while (res.next()) {
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

    public Court getCourtById(long id) {
        Court court = null;
        try {
            String sql = "SELECT * FROM courts WHERE id = (?)";
            PreparedStatement statement = databaseConfig.getConnection().prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet res = statement.executeQuery(sql);
            court = new Court();
            if (res.next()) {
                court.setId(res.getInt("id"));
                court.setNameCourt(res.getString("court_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return court;
    }

    public List<Integer> getBookingTimeForCourt(long id, LocalDate date) throws SQLException {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT * FROM booking_courts WHERE court_id = (?) AND date = (?)";
        PreparedStatement statement = databaseConfig.getConnection().prepareStatement(sql);
        statement.setLong(1, id);
        statement.setDate(2, Date.valueOf(date)); // конвертация LocalDate в java.sql.Date
        ResultSet res = statement.executeQuery();
        while (res.next()) {
            list.add(res.getInt("time"));
        }
        return list;
    }

    public Long addBookingTimeForCourt(long courtId, LocalDate date, int hour) throws SQLException {
        try {
            String sql = "INSERT INTO booking_courts (court_id, date, time) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = databaseConfig.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, courtId);
            preparedStatement.setDate(2, Date.valueOf(date)); // конвертация LocalDate в java.sql.Date
            preparedStatement.setInt(3, hour);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    }
                }
            } else {
                throw new SQLException("No rows affected.");
                //todo
            }
        } catch (SQLException e) {
            //todo
        }
        return null;
    }
}
