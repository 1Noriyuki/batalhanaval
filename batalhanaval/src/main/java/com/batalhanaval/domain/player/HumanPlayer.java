package com.batalhanaval.domain.player;

import com.batalhanaval.domain.model.Coordinate;

public class HumanPlayer extends Player {
    public HumanPlayer(String name, int boardSize) {
        super(name, boardSize);
    }

    @Override
    public Coordinate chooseShot() {
        // A leitura real é feita via UI para manter o desacoplamento de Scanner
        return null; 
    }
}