package com.batalhanaval.domain.player;

import com.batalhanaval.domain.model.Coordinate;
import java.util.ArrayDeque;
import java.util.Random;

public class CpuPlayer extends Player {
    private final Random rng;
    private final boolean[][] tried;
    private final ArrayDeque<Coordinate> targets;
    private final int size;

    public CpuPlayer(String name, int boardSize, Random rng) {
        super(name, boardSize);
        this.size = boardSize;
        this.rng = rng;
        this.tried = new boolean[size][size];
        this.targets = new ArrayDeque<>();
    }

    @Override
    public Coordinate chooseShot() {
        // 1. Ataca alvos pendentes na fila (Modo HUNT)
        while (!targets.isEmpty()) {
            Coordinate t = targets.removeFirst();
            if (isValidTarget(t)) {
                markTried(t);
                return t;
            }
        }

        // 2. Modo Padrão / Paridade (Aleatório estruturado)
        for (int tries = 0; tries < 5000; tries++) {
            int x = rng.nextInt(size);
            int y = rng.nextInt(size);
            if (!tried[y][x]) {
                if ((x + y) % 2 == 0 || rng.nextInt(100) < 25) {
                    Coordinate coord = new Coordinate(x, y);
                    markTried(coord);
                    return coord;
                }
            }
        }

        // Fallback linear
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (!tried[y][x]) {
                    Coordinate coord = new Coordinate(x, y);
                    markTried(coord);
                    return coord;
                }
            }
        }
        return null;
    }

    public void addTargetCandidates(Coordinate base) {
        int x = base.getX();
        int y = base.getY();
        targets.addLast(new Coordinate(x + 1, y));
        targets.addLast(new Coordinate(x - 1, y));
        targets.addLast(new Coordinate(x, y + 1));
        targets.addLast(new Coordinate(x, y - 1));
    }

    public void clearTargets() {
        targets.clear();
    }

    private boolean isValidTarget(Coordinate c) {
        return c.getX() >= 0 && c.getX() < size && c.getY() >= 0 && c.getY() < size && !tried[c.getY()][c.getX()];
    }

    private void markTried(Coordinate c) {
        tried[c.getY()][c.getX()] = true;
    }
}