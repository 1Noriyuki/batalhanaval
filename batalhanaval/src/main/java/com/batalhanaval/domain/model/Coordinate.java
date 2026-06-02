package com.batalhanaval.domain.model;

import java.util.Locale;

public class Coordinate {
    private final int x;
    private final int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Coordinate parse(String input, int boardSize) {
        if (input == null) return null;
        String t = input.trim().toUpperCase(Locale.ROOT).replace(" ", "");
        if (t.length() < 2 || t.length() > 3) return null;

        char col = t.charAt(0);
        if (col < 'A' || col > (char)('A' + boardSize - 1)) return null;
        int x = col - 'A';

        try {
            int row = Integer.parseInt(t.substring(1));
            if (row < 1 || row > boardSize) return null;
            int y = row - 1;
            return new Coordinate(x, y);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public String toString() {
        return "" + (char)('A' + x) + (y + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}