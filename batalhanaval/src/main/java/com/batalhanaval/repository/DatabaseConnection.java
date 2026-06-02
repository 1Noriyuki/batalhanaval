package com.batalhanaval.repository;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection(String dbPath) throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Garante de forma programática a existência do diretório pai
            File dbFile = new File(dbPath);
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            String url = "jdbc:sqlite:" + dbPath;
            connection = DriverManager.getConnection(url);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão com o banco: " + e.getMessage());
            }
        }
    }
}