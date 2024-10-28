//package org.example.sport_section.DataBase;
//
//import org.springframework.stereotype.Component;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.Properties;
//
//@Component
//public class DatabaseConfig {
//    private static String URL;
//    private static String USERNAME;
//    private static String PASSWORD;
//    private static Connection connection;
//
//    static {
//        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
//            Properties prop = new Properties();
//            prop.load(input);
//
//            URL = prop.getProperty("spring.datasource.url");
//            USERNAME = prop.getProperty("spring.datasource.username");
//            PASSWORD = prop.getProperty("spring.datasource.password");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        try
//        {
//            Class.forName("org.postgresql.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//    public Connection getConnection() {
//        return connection;
//    }
//}
