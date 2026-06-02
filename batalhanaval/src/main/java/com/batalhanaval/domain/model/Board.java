package com.batalhanaval.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int size;
    private final char[][] grid;
    private final int[][] shipIds;
    private final List<Ship> fleet;

    public Board(int size) {
        this.size = size;
        this.grid = new char[size][size];
        this.shipIds = new int[size][size];
        this.fleet = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = '.';
                shipIds[i][j] = -1;
            }
        }
    }

    public boolean canPlace(int x, int y, int len, boolean horiz) {
        if (horiz) {
            if (x + len > size) return false;
            for (int i = 0; i < len; i++) {
                if (grid[y][x + i] != '.') return false;
            }
        } else {
            if (y + len > size) return false;
            for (int i = 0; i < len; i++) {
                if (grid[y + i][x] != '.') return false;
            }
        }
        return true;
    }

    public void placeShip(Ship ship, int x, int y, boolean horiz) {
        for (int i = 0; i < ship.getSize(); i++) {
            int cx = horiz ? x + i : x;
            int cy = horiz ? y : y + i;
            grid[cy][cx] = 'S';
            shipIds[cy][cx] = ship.getId();
            ship.addCoordinate(new Coordinate(cx, cy));
        }
        fleet.add(ship);
    }

    public ShotResult receiveShot(Coordinate coord) {
        int x = coord.getX();
        int y = coord.getY();

        if (grid[y][x] == 'S') {
            grid[y][x] = 'X';
            int shipId = shipIds[y][x];
            Ship ship = findShipById(shipId);
            if (ship != null) {
                ship.takeHit();
                if (ship.isSunk()) return ShotResult.AFUNDOU;
            }
            return ShotResult.ACERTO;
        } else if (grid[y][x] == '.') {
            grid[y][x] = 'o';
            return ShotResult.AGUA;
        }
        return ShotResult.AGUA; 
    }

    public boolean allShipsSunk() {
        if (fleet.isEmpty()) return false;
        for (Ship ship : fleet) {
            if (!ship.isSunk()) return false;
        }
        return true;
    }

    public int getRemainingShipsCount() {
        int count = 0;
        for (Ship ship : fleet) {
            if (!ship.isSunk()) count++;
        }
        return count;
    }

    public Ship getShipAt(Coordinate coord) {
        int id = shipIds[coord.getY()][coord.getX()];
        return findShipById(id);
    }

    private Ship findShipById(int id) {
        for (Ship s : fleet) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    public char[][] getGrid() { return grid; }
    public int getSize() { return size; }
}