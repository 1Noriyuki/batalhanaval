package com.batalhanaval.domain.player;

import com.batalhanaval.domain.model.Board;
import com.batalhanaval.domain.model.Coordinate;

public abstract class Player {
    protected final String name;
    protected final Board board;
    protected final Board shotBoard; // Armazena o mapa de tiros efetuados contra o inimigo

    public Player(String name, int boardSize) {
        this.name = name;
        this.board = new Board(boardSize);
        this.shotBoard = new Board(boardSize);
    }

    public abstract Coordinate chooseShot();

    public String getName() { return name; }
    public Board getBoard() { return board; }
    public Board getShotBoard() { return shotBoard; }
}