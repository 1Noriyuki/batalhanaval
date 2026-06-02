package com.batalhanaval.service;

import com.batalhanaval.domain.model.Board;
import com.batalhanaval.domain.model.Coordinate;
import com.batalhanaval.domain.model.Ship;
import java.util.ArrayList;
import java.util.List;

public class FleetValidator {

    public static class ValidationResult {
        private final boolean ok;
        private final List<String> errors;

        public ValidationResult(boolean ok, List<String> errors) {
            this.ok = ok;
            this.errors = errors;
        }
        public boolean isOk() { return ok; }
        public List<String> getErrors() { return errors; }
    }

    public ValidationResult validate(List<Ship> fleet, Board board, String adjacencyRule, int[] expectedSizes) {
        List<String> errors = new ArrayList<>();

        if (fleet.size() != expectedSizes.length) {
            errors.add("Quantidade de navios incorreta.");
        }

        char[][] grid = board.getGrid();
        int size = board.getSize();

        for (Ship ship : fleet) {
            for (Coordinate coord : ship.getCoordinates()) {
                int x = coord.getX();
                int y = coord.getY();

                if (x < 0 || x >= size || y < 0 || y >= size) {
                    errors.add("Navio fora das células limites: " + ship.getName());
                    continue;
                }

                if (hasAdjacencyViolation(x, y, grid, adjacencyRule, ship.getId())) {
                    errors.add("Sobreposição ou proximidade ilegal: " + ship.getName());
                }
            }
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    private boolean hasAdjacencyViolation(int x, int y, char[][] grid, String rule, int currentShipId) {
        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, 1, -1, -1};

        int limit = rule.equals("NONE") ? 0 : (rule.equals("ORTHO") ? 4 : 8);

        for (int i = 0; i < limit; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];

            if (nx >= 0 && nx < grid.length && ny >= 0 && ny < grid.length) {
                if (grid[ny][nx] == 'S' || grid[ny][nx] == 'X') {
                    return true;
                }
            }
        }
        return false;
    }
}