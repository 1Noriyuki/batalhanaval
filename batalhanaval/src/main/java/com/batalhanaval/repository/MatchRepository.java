package com.batalhanaval.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchRepository {
    private static final String URL = "jdbc:sqlite:data/batalha_naval.db";

    public MatchRepository() {
        // Garante a existência do diretório data/
        java.io.File dir = new java.io.File("data");
        if (!dir.exists()) dir.mkdirs();
        
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS partidas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "vencedor TEXT NOT NULL," +
                "data TEXT NOT NULL" +
                ");");
            stmt.execute("CREATE TABLE IF NOT EXISTS jogadas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_partida INTEGER," +
                "jogador TEXT," +
                "coordenada TEXT," +
                "resultado TEXT" +
                ");");
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar o SQLite: " + e.getMessage());
        }
    }

    public int saveMatch(String winner) {
        String sql = "INSERT INTO partidas(vencedor, data) VALUES(?, datetime('now'));";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, winner);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar partida: " + e.getMessage());
        }
        return -1;
    }

    public void updateMatch(int matchId, String winner) {
        String sql = "UPDATE partidas SET vencedor = ? WHERE id = ?;";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, winner);
            pstmt.setInt(2, matchId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar partida: " + e.getMessage());
        }
    }

    public void saveMove(int matchId, String player, String coord, String result) {
        String sql = "INSERT INTO jogadas(id_partida, jogador, coordenada, resultado) VALUES(?, ?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, matchId);
            pstmt.setString(2, player);
            pstmt.setString(3, coord);
            pstmt.setString(4, result);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar jogada: " + e.getMessage());
        }
    }

    public List<String> getHistory() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT id, vencedor, data FROM partidas ORDER BY id DESC;";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add("Partida #" + rs.getInt("id") + " | Vencedor: " + rs.getString("vencedor") + " | Data: " + rs.getString("data"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler histórico: " + e.getMessage());
        }
        return list;
    }
}