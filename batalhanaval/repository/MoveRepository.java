package com.batalhanaval.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoveRepository {
    private final Connection connection;

    public MoveRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Salva uma jogada individual no banco de dados.
     */
    public void save(int matchId, String player, String coordinate, String result) throws SQLException {
        String query = "INSERT INTO jogadas (id_partida, jogador, coordenada, resultado) VALUES (?, ?, ?, ?);";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, matchId);
            ps.setString(2, player);
            ps.setString(3, coordinate);
            ps.setString(4, result);
            ps.executeUpdate();
        }
    }

    /**
     * Recupera todas as jogadas de uma partida específica em ordem cronológica.
     * Muito útil para o modo 'REPLAY' configurado no game.properties.
     */
    public List<String> getMovesByMatch(int matchId) throws SQLException {
        List<String> moves = new ArrayList<>();
        String query = "SELECT jogador, coordenada, resultado FROM jogadas WHERE id_partida = ? ORDER BY id ASC;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, matchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    moves.add(String.format("[%s] Atirou em %s -> %s", 
                        rs.getString("jogador"), 
                        rs.getString("coordenada"), 
                        rs.getString("resultado")
                    ));
                }
            }
        }
        return moves;
    }
}