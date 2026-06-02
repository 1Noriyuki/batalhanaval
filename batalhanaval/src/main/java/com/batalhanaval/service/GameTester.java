package com.batalhanaval.service;

import com.batalhanaval.domain.model.Board;
import com.batalhanaval.domain.model.Coordinate;
import com.batalhanaval.domain.model.Ship;
import com.batalhanaval.domain.model.ShotResult;
import com.batalhanaval.domain.player.CpuPlayer;
import com.batalhanaval.domain.player.Player;
import com.batalhanaval.repository.MatchRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameTester {
    private static final int BOARD_SIZE = 10;
    private static final int[] SHIP_SIZES = {5, 4, 3, 3, 2};
    private static final String[] SHIP_NAMES = {"Porta-avioes", "Encouracado", "Cruzador", "Submarino", "Destroyer"};
    
    private List<String> testLog;
    private MatchRepository repository;

    public GameTester() {
        this.testLog = new ArrayList<>();
        this.repository = new MatchRepository();
    }

    public void runFullTest() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("INICIANDO TESTE AUTOMATIZADO - CPU vs CPU");
        System.out.println("=".repeat(60) + "\n");

        testLog.clear();
        long startTime = System.currentTimeMillis();

        try {
            // Test 1: Initialize game
            addLog("✓ Inicializando jogo...");
            Random rng = new Random(12345); // Fixed seed for reproducibility
            CpuPlayer cpu1 = new CpuPlayer("CPU-1", BOARD_SIZE, rng);
            CpuPlayer cpu2 = new CpuPlayer("CPU-2", BOARD_SIZE, new Random(54321));
            GameEngine engine = new GameEngine(cpu1, cpu2);
            addLog("✓ Jogo inicializado com sucesso");

            // Test 2: Fleet placement
            addLog("\n[TESTE 2] Posicionamento automático de frotas...");
            autoPlaceFleet(cpu1.getBoard(), rng);
            autoPlaceFleet(cpu2.getBoard(), new Random(99999));
            addLog("✓ CPU-1: " + cpu1.getBoard().getRemainingShipsCount() + " navios posicionados");
            addLog("✓ CPU-2: " + cpu2.getBoard().getRemainingShipsCount() + " navios posicionados");

            // Test 3: Game loop
            addLog("\n[TESTE 3] Executando loop principal do jogo...");
            int turnCount = 0;
            int maxTurns = 500; // Safety limit to prevent infinite loops

            while (engine.isActive() && turnCount < maxTurns) {
                turnCount++;

                if (engine.isPlayerTurn()) {
                    // CPU-1 shoots
                    Coordinate target = cpu1.chooseShot();
                    if (target != null) {
                        ShotResult result = cpu2.getBoard().receiveShot(target);
                        updateShotGrid(cpu1.getShotBoard(), target, result);

                        if (result == ShotResult.ACERTO || result == ShotResult.AFUNDOU) {
                            cpu1.addTargetCandidates(target);
                        }
                    }
                } else {
                    // CPU-2 shoots
                    Coordinate target = cpu2.chooseShot();
                    if (target != null) {
                        ShotResult result = cpu1.getBoard().receiveShot(target);
                        updateShotGrid(cpu2.getShotBoard(), target, result);

                        if (result == ShotResult.ACERTO || result == ShotResult.AFUNDOU) {
                            cpu2.addTargetCandidates(target);
                        }
                    }
                }

                engine.checkGameOver();
                engine.switchTurn();
            }

            addLog("✓ Loop principal executado: " + turnCount + " turnos");

            // Test 4: Game over detection
            addLog("\n[TESTE 4] Detecção de fim de jogo...");
            if (!engine.isActive()) {
                addLog("✓ Jogo finalizou corretamente após " + turnCount + " turnos");
            } else {
                addLog("✗ FALHA: Jogo não finalizou (limite de turnos atingido)");
            }

            // Test 5: Winner determination
            addLog("\n[TESTE 5] Determinação de vencedor...");
            Player winner = engine.getWinner();
            if (winner != null) {
                addLog("✓ Vencedor determinado: " + winner.getName());
                addLog("✓ Navios restantes - " + winner.getName() + ": " + winner.getBoard().getRemainingShipsCount());
            } else {
                addLog("✗ FALHA: Nenhum vencedor determinado");
            }

            // Test 6: Database operations
            addLog("\n[TESTE 6] Operações de banco de dados...");
            addLog("→ Tentando salvar partida com vencedor: " + (winner != null ? winner.getName() : "ERRO"));
            int matchId = repository.saveMatch("TESTE");
            if (matchId > 0) {
                addLog("✓ Partida salva com ID: " + matchId);
                addLog("→ Tentando atualizar partida com ID: " + matchId);
                repository.updateMatch(matchId, winner != null ? winner.getName() : "ERRO");
                addLog("✓ Partida atualizada com sucesso");
                addLog("✓ Vencedor registrado: " + (winner != null ? winner.getName() : "ERRO"));
            } else {
                addLog("✗ FALHA: Não conseguiu salvar partida (matchId = -1)");
                addLog("✗ Verifique se o arquivo 'data/batalha_naval.db' foi criado");
                addLog("✗ Verifique se há permissão de escrita na pasta");
            }

            // Test 7: Remaining ships validation
            addLog("\n[TESTE 7] Validação de navios restantes...");
            int remainingCpu1 = cpu1.getBoard().getRemainingShipsCount();
            int remainingCpu2 = cpu2.getBoard().getRemainingShipsCount();
            addLog("✓ CPU-1 navios restantes: " + remainingCpu1);
            addLog("✓ CPU-2 navios restantes: " + remainingCpu2);

            if ((remainingCpu1 == 0) || (remainingCpu2 == 0)) {
                addLog("✓ Validação OK: Pelo menos um jogador perdeu todos os navios");
            } else {
                addLog("✗ FALHA: Ambos os jogadores ainda têm navios");
            }

            long elapsedTime = System.currentTimeMillis() - startTime;

            // Print final boards
            addLog("\n[TESTE 8] Estado final dos tabuleiros...\n");
            printFinalBoards(cpu1, cpu2);

            // Print summary
            printTestSummary(elapsedTime);

        } catch (Exception e) {
            addLog("\n✗ ERRO: " + e.getMessage());
            e.printStackTrace();
            printTestSummary(System.currentTimeMillis() - startTime);
        }
    }

    private void autoPlaceFleet(Board board, Random rng) {
        for (int i = 0; i < SHIP_SIZES.length; i++) {
            boolean placed = false;
            while (!placed) {
                int x = rng.nextInt(board.getSize());
                int y = rng.nextInt(board.getSize());
                boolean horiz = rng.nextBoolean();
                if (board.canPlace(x, y, SHIP_SIZES[i], horiz)) {
                    Ship ship = new Ship(i, SHIP_NAMES[i], SHIP_SIZES[i]);
                    board.placeShip(ship, x, y, horiz);
                    placed = true;
                }
            }
        }
    }

    private void updateShotGrid(Board shotBoard, Coordinate target, ShotResult result) {
        char mark = (result == ShotResult.AGUA) ? 'o' : 'X';
        shotBoard.getGrid()[target.getY()][target.getX()] = mark;
    }

    private void addLog(String message) {
        testLog.add(message);
        System.out.println(message);
    }

    private void printTestSummary(long elapsedTime) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("RESULTADO DO TESTE");
        System.out.println("=".repeat(60));
        System.out.println("\nTempo de execução: " + elapsedTime + " ms");
        System.out.println("Total de linhas de log: " + testLog.size());
        System.out.println("\n[LOG COMPLETO]");
        for (String log : testLog) {
            System.out.println(log);
        }
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TESTE CONCLUÍDO");
        System.out.println("=".repeat(60) + "\n");
    }

    public List<String> getTestLog() {
        return testLog;
    }

    private void printFinalBoards(CpuPlayer cpu1, CpuPlayer cpu2) {
        System.out.println("\n=== TABULEIROS FINAIS ===\n");
        
        System.out.println("CPU-1 - TABULEIRO PRÓPRIO vs TIROS NO INIMIGO");
        printTwoBoards(cpu1.getBoard(), cpu1.getShotBoard());
        
        System.out.println("\n\nCPU-2 - TABULEIRO PRÓPRIO vs TIROS NO INIMIGO");
        printTwoBoards(cpu2.getBoard(), cpu2.getShotBoard());
    }

    private void printTwoBoards(Board ownBoard, Board shotBoard) {
        int size = ownBoard.getSize();
        System.out.println("%-30s | %s".formatted("TABULEIRO PRÓPRIO", "TIROS NO INIMIGO"));
        System.out.println("    A B C D E F G H I J     |     A B C D E F G H I J");

        char[][] ownGrid = ownBoard.getGrid();
        char[][] shotGrid = shotBoard.getGrid();

        for (int y = 0; y < size; y++) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%2d  ", (y + 1)));
            for (int x = 0; x < size; x++) sb.append(ownGrid[y][x]).append(" ");
            sb.append("    |  ");
            sb.append(String.format("%2d  ", (y + 1)));
            for (int x = 0; x < size; x++) sb.append(shotGrid[y][x]).append(" ");
            System.out.println(sb.toString());
        }
        System.out.println("Legenda: S navio, X acerto, o agua, . vazio/desconhecido");
    }
}
