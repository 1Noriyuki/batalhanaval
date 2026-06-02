package com.batalhanaval.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private final int id;
    private final String name;
    private final int size;
    private final List<Coordinate> coordinates;
    private int hp;

    public Ship(int id, String name, int size) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.coordinates = new ArrayList<>();
        this.hp = size;
    }

    public void addCoordinate(Coordinate coord) {
        this.coordinates.add(coord);
    }

    public void takeHit() {
        if (hp > 0) hp--;
    }

    public boolean isSunk() {
        return hp == 0;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getSize() { return size; }
    public List<Coordinate> getCoordinates() { return coordinates; }
}