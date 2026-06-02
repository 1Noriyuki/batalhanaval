package com.batalhanaval.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GameProperties {
    private final Properties props = new Properties();

    private int boardSize = 10;
    private int[] fleetSizes = {5, 4, 3, 3, 2};
    private String[] fleetNames = {"Porta-avioes", "Encouracado", "Cruzador", "Submarino", "Destroyer"};
    private String fleetAdjacencyRule = "NONE";
    private boolean hitGrantsExtraShot = false;
    private int maxExtraShots = 0;
    private String cpuStrategy = "RANDOM";
    private boolean cpuUseParityPreference = false;
    private boolean uiShowOwnShips = true;
    private boolean uiShowLegend = true;
    private int uiReplayDelayMs = 0;
    private boolean dbEnabled = false;
    private String dbType = "sqlite";
    private String dbSqliteFile = "data/batalha_naval.db";
    private boolean dbAutoMigrate = true;
    private boolean dbSaveInitialFleet = false;
    private String gameSeed = "";
    private String gameMode = "PLAY";

    public GameProperties(String filePath) {
        try (InputStream input = new FileInputStream(filePath)) {
            props.load(input);
            parseProperties();
        } catch (IOException ex) {
            System.err.println("Aviso: Arquivo '" + filePath + "' não encontrado ou ilegível. Usando padrões.");
        }
    }

    private void parseProperties() {
        this.boardSize = parseInt(props.getProperty("board.size"), 10);

        String sizesStr = props.getProperty("fleet.sizes", "5,4,3,3,2");
        String[] parsedSizes = sizesStr.split(",");
        this.fleetSizes = new int[parsedSizes.length];
        for (int i = 0; i < parsedSizes.length; i++) {
            int size = parseInt(parsedSizes[i], 1);
            this.fleetSizes[i] = size > 0 ? size : 1;
        }

        String namesStr = props.getProperty("fleet.names", "Porta-avioes,Encouracado,Cruzador,Submarino,Destroyer");
        this.fleetNames = namesStr.split(",");
        for (int i = 0; i < this.fleetNames.length; i++) {
            this.fleetNames[i] = this.fleetNames[i].trim();
        }

        this.fleetAdjacencyRule = props.getProperty("fleet.adjacency_rule", "NONE").toUpperCase();
        this.hitGrantsExtraShot = parseBoolean(props.getProperty("rules.hit_grants_extra_shot"), false);
        this.maxExtraShots = parseInt(props.getProperty("rules.max_extra_shots"), 0);
        this.cpuStrategy = props.getProperty("cpu.strategy", "RANDOM").toUpperCase();
        this.cpuUseParityPreference = parseBoolean(props.getProperty("cpu.use_parity_preference"), false);
        this.uiShowOwnShips = parseBoolean(props.getProperty("ui.show_own_ships"), true);
        this.uiShowLegend = parseBoolean(props.getProperty("ui.show_legend"), true);
        this.uiReplayDelayMs = parseInt(props.getProperty("ui.replay_delay_ms"), 0);
        this.dbEnabled = parseBoolean(props.getProperty("db.enabled"), false);
        this.dbType = props.getProperty("db.type", "sqlite");
        this.dbSqliteFile = props.getProperty("db.sqlite.file", "data/batalha_naval.db");
        this.dbAutoMigrate = parseBoolean(props.getProperty("db.auto_migrate"), true);
        this.dbSaveInitialFleet = parseBoolean(props.getProperty("db.save_initial_fleet"), false);
        this.gameSeed = props.getProperty("game.seed", "");
        this.gameMode = props.getProperty("game.mode", "PLAY").toUpperCase();
    }

    private int parseInt(String value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private boolean parseBoolean(String value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    // Getters
    public int getBoardSize() { return boardSize; }
    public int[] getFleetSizes() { return fleetSizes; }
    public String[] getFleetNames() { return fleetNames; }
    public String getFleetAdjacencyRule() { return fleetAdjacencyRule; }
    public boolean isHitGrantsExtraShot() { return hitGrantsExtraShot; }
    public int getMaxExtraShots() { return maxExtraShots; }
    public String getCpuStrategy() { return cpuStrategy; }
    public boolean isCpuUseParityPreference() { return cpuUseParityPreference; }
    public boolean isUiShowOwnShips() { return uiShowOwnShips; }
    public boolean isUiShowLegend() { return uiShowLegend; }
    public int getUiReplayDelayMs() { return uiReplayDelayMs; }
    public boolean isDbEnabled() { return dbEnabled; }
    public String getDbType() { return dbType; }
    public String getDbSqliteFile() { return dbSqliteFile; }
    public boolean isDbAutoMigrate() { return dbAutoMigrate; }
    public boolean isDbSaveInitialFleet() { return dbSaveInitialFleet; }
    public String getGameSeed() { return gameSeed; }
    public String getGameMode() { return gameMode; }
}
