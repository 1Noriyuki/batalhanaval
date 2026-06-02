package com.batalhanaval.ui;

import com.batalhanaval.domain.model.*;
import com.batalhanaval.domain.player.*;
import com.batalhanaval.service.*;
import com.batalhanaval.repository.MatchRepository;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MatchRepository repository = new MatchRepository();

        System.out.println("=== BATALHA NAVAL REFACTOR (OO) ===");
        System.out.println("1) Jogar Nova Partida");
        System.out.println("2) Ver Histórico de Partidas");
        System.out.println("3) Teste Automático (CPU vs CPU)");
        System.out.println("4) Sair");
        System.out.print("> ");
        String menuOpt = sc.nextLine().trim();

        if ("2".equals(menuOpt)) {
            List<String> history = repository.getHistory();
            if (history.isEmpty()) System.out.println("Nenhuma partida gravada.");
            else history.forEach(System.out::println);
            return;
        }
        else if ("3".equals(menuOpt)) {
            GameTester tester = new GameTester();
            tester.runFullTest();
            return;
        }
        else if (!"1".equals(menuOpt)) {
            System.out.println("Encerrando...");
            return;
        }
        System.out.print("Seed (vazio para aleatório): ");
        String seedStr = sc.nextLine().trim();
        Random rng = seedStr.isEmpty() ? new Random() : new Random(seedStr.hashCode());

        final int N = 10;
        final int[] shipSizes = {5, 4, 3, 3, 2};
        final String[] shipNames = {"Porta-avioes", "Encouracado", "Cruzador", "Submarino", "Destroyer"};

        HumanPlayer human = new HumanPlayer("Jogador", N);
        CpuPlayer cpu = new CpuPlayer("CPU", N, rng);
        GameEngine engine = new GameEngine(human, cpu);
        List<String> gameLog = new ArrayList<>();

        // Posicionamento Manual ou Automático do Humano
        System.out.print("Deseja posicionar manualmente? (s/n): ");
        String manual = sc.nextLine().trim().toLowerCase(Locale.ROOT);

        if (manual.equals("s") || manual.equals("sim")) {
            for (int i = 0; i < shipSizes.length; i++) {
                boolean placed = false;
                while (!placed) {
                    printSingleBoard("SEU TABULEIRO", human.getBoard().getGrid());
                    System.out.println("Posicione: " + shipNames[i] + " (Tamanho " + shipSizes[i] + ")");
                    System.out.print("Coordenada inicial (Ex: A1): ");
                    Coordinate coord = Coordinate.parse(sc.nextLine(), N);
                    if (coord == null) {
                        System.out.println("Coordenada inválida.");
                        continue;
                    }
                    System.out.print("Direção (H para horizontal, V para vertical): ");
                    String dir = sc.nextLine().trim().toUpperCase(Locale.ROOT);
                    boolean horiz = dir.equals("H");

                    if (!human.getBoard().canPlace(coord.getX(), coord.getY(), shipSizes[i], horiz)) {
                        System.out.println("Posição inválida (Colisão ou fora dos limites).");
                        continue;
                    }

                    Ship ship = new Ship(i, shipNames[i], shipSizes[i]);
                    human.getBoard().placeShip(ship, coord.getX(), coord.getY(), horiz);
                    placed = true;
                }
            }
        } else {
            autoPlaceFleet(human.getBoard(), shipSizes, shipNames, rng);
            System.out.println("Sua frota foi posicionada de forma automática.");
        }

        // Posicionamento automático da CPU
        autoPlaceFleet(cpu.getBoard(), shipSizes, shipNames, rng);

        // Loop Principal da Partida
        int matchId = repository.saveMatch("EM ANDAMENTO");

        while (engine.isActive()) {
            printTwoBoards(human.getBoard(), human.getShotBoard());
            System.out.println("Navios restantes -> Você: " + human.getBoard().getRemainingShipsCount() + " | CPU: " + cpu.getBoard().getRemainingShipsCount());

            if (engine.isPlayerTurn()) {
                System.out.println("\n--- SEU TURNO ---");
                System.out.println("Ações: 1) Atirar | 2) Ver últimos logs | 3) Sair");
                System.out.print("> ");
                String action = sc.nextLine().trim();
                
                if ("3".equals(action)) {
                    System.out.println("Encerrando partida...");
                    repository.updateMatch(matchId, "ABORTADA");
                    return;
                }

                if ("2".equals(action)) {
                    int start = Math.max(0, gameLog.size() - 5);
                    for (int i = start; i < gameLog.size(); i++) System.out.println(gameLog.get(i));
                    continue;
                }

                System.out.print("Informe a coordenada do tiro (Ex: B7): ");
                Coordinate target = Coordinate.parse(sc.nextLine(), N);
                if (target == null || human.getShotBoard().getGrid()[target.getY()][target.getX()] != '.') {
                    System.out.println("Coordenada inválida ou já alvejada.");
                    continue;
                }

                ShotResult result = cpu.getBoard().receiveShot(target);
                updateShotGrid(human.getShotBoard(), target, result);

                String msg = "Você atirou em " + target + " -> " + result;
                System.out.println(msg);
                gameLog.add(msg);
                repository.saveMove(matchId, human.getName(), target.toString(), result.name());

                engine.switchTurn();
            } else {
                System.out.println("\n--- TURNO DA CPU ---");
                Coordinate cpuTarget = cpu.chooseShot();
                if (cpuTarget == null) {
                    System.out.println("Erro: CPU não conseguiu escolher um alvo válido.");
                    engine.switchTurn();
                    continue;
                }
                
                ShotResult result = human.getBoard().receiveShot(cpuTarget);
                updateShotGrid(cpu.getShotBoard(), cpuTarget, result);

                if (result == ShotResult.ACERTO || result == ShotResult.AFUNDOU) {
                    cpu.addTargetCandidates(cpuTarget);
                }

                String msg = "CPU atirou em " + cpuTarget + " -> " + result;
                System.out.println(msg);
                gameLog.add(msg);
                repository.saveMove(matchId, cpu.getName(), cpuTarget.toString(), result.name());

                engine.switchTurn();
            }
            engine.checkGameOver();
        }

        Player winner = engine.getWinner();
        System.out.println("\n=== FIM DE JOGO ===");
        
        if (winner != null) {
            System.out.println("Vencedor: " + winner.getName());
            repository.updateMatch(matchId, winner.getName());
        } else {
            System.out.println("Erro: Nenhum vencedor determinado.");
            repository.updateMatch(matchId, "ERRO");
        }
        
        sc.close();
    }

    private static void autoPlaceFleet(Board board, int[] sizes, String[] names, Random rng) {
        for (int i = 0; i < sizes.length; i++) {
            boolean placed = false;
            while (!placed) {
                int x = rng.nextInt(board.getSize());
                int y = rng.nextInt(board.getSize());
                boolean horiz = rng.nextBoolean();
                if (board.canPlace(x, y, sizes[i], horiz)) {
                    Ship ship = new Ship(i, names[i], sizes[i]);
                    board.placeShip(ship, x, y, horiz);
                    placed = true;
                }
            }
        }
    }

    private static void updateShotGrid(Board shotBoard, Coordinate target, ShotResult result) {
        char mark = (result == ShotResult.AGUA) ? 'o' : 'X';
        shotBoard.getGrid()[target.getY()][target.getX()] = mark;
    }

    private static void printSingleBoard(String title, char[][] grid) {
        System.out.println("\n" + title);
        System.out.println("    A B C D E F G H I J");
        for (int y = 0; y < grid.length; y++) {
            System.out.printf("%2d  ", (y + 1));
            for (int x = 0; x < grid[y].length; x++) {
                System.out.print(grid[y][x] + " ");
            }
            System.out.println();
        }
    }

    private static void printTwoBoards(Board ownBoard, Board shotBoard) {
        int size = ownBoard.getSize();
        System.out.println("\n%-27s | %s".formatted("SEU TABULEIRO", "SEUS TIROS NO INIMIGO"));
        System.out.println("    A B C D E F G H I J     |      A B C D E F G H I J");

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