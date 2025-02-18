package com.main.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDB {

    private static MyDB instance;
    private Connection connection;

    private final String URL = "jdbc:mysql://localhost:3306/evenix";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    private MyDB() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to DB");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static MyDB getInstance() {
        if(instance == null)
            instance = new MyDB();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
