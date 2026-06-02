package com.batalhanaval.domain.model;

import java.time.LocalDateTime;

public class Match {
    private int id;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private String winnerName;
    private final String seed;

    public Match(String seed) {
        this.startTime = LocalDateTime.now();
        this.winnerName = "EM ANDAMENTO";
        this.seed = seed;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getStartTime() { return startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void endMatch(String winnerName) {
        this.endTime = LocalDateTime.now();
        this.winnerName = winnerName;
    }

    public String getWinnerName() { return winnerName; }
    public String getSeed() { return seed; }
}