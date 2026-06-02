package com.batalhanaval;

import java.sql.*;

public class DataViewer {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:data/batalha_naval.db";
        
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("=== PARTIDAS ===\n");
            ResultSet rs = stmt.executeQuery("SELECT * FROM partidas;");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + 
                                 " | Vencedor: " + rs.getString("vencedor") + 
                                 " | Data: " + rs.getString("data"));
            }
            
            System.out.println("\n=== JOGADAS ===\n");
            rs = stmt.executeQuery("SELECT * FROM jogadas;");
            while (rs.next()) {
                System.out.println("Partida: " + rs.getInt("id_partida") + 
                                 " | Jogador: " + rs.getString("jogador") + 
                                 " | Coord: " + rs.getString("coordenada") + 
                                 " | Resultado: " + rs.getString("resultado"));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}
