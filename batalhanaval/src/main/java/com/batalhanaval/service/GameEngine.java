package com.batalhanaval.service;

import com.batalhanaval.domain.player.Player;

public class GameEngine {
    private final Player humanPlayer;
    private final Player cpuPlayer;
    private boolean playerTurn;
    private boolean active;

    public GameEngine(Player humanPlayer, Player cpuPlayer) {
        this.humanPlayer = humanPlayer;
        this.cpuPlayer = cpuPlayer;
        this.playerTurn = true;
        this.active = true;
    }

    public void checkGameOver() {
        if (humanPlayer.getBoard().allShipsSunk() || cpuPlayer.getBoard().allShipsSunk()) {
            this.active = false;
        }
    }

    public boolean isPlayerTurn() { return playerTurn; }
    public void switchTurn() { this.playerTurn = !this.playerTurn; }
    public boolean isActive() { return active; }
    public Player getWinner() {
        if (cpuPlayer.getBoard().allShipsSunk()) return humanPlayer;
        if (humanPlayer.getBoard().allShipsSunk()) return cpuPlayer;
        return null;
    }
}