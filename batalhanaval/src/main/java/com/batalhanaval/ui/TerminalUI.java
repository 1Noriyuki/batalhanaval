package com.batalhanaval.ui;

import com.batalhanaval.domain.model.Board;
import java.util.Scanner;

public class TerminalUI {
    private final Scanner scanner;

    public TerminalUI() {
        this.scanner = new Scanner(System.in);
    }

    public void printTwoBoards(Board ownBoard, Board shotBoard) {
        int size = ownBoard.getSize();
        System.out.println("\n=================================================================");
        System.out.printf("%-30s | %s\n", "SEU TABULEIRO (FROTA)", "SEUS TIROS NO INIMIGO");
        
        // Monta o cabeçalho dinâmico baseado no tamanho (A-J)
        StringBuilder header = new StringBuilder("    ");
        for (int i = 0; i < size; i++) {
            header.append((char) ('A' + i)).append(" ");
        }
        System.out.println(header + "    | " + header);

        char[][] ownGrid = ownBoard.getGrid();
        char[][] shotGrid = shotBoard.getGrid();

        for (int y = 0; y < size; y++) {
            // Tabuleiro Esquerdo (Jogador)
            StringBuilder leftRow = new StringBuilder(String.format("%2d  ", (y + 1)));
            for (int x = 0; x < size; x++) {
                leftRow.append(ownGrid[y][x]).append(" ");
            }

            // Tabuleiro Direito (Tiros disparados)
            StringBuilder rightRow = new StringBuilder(String.format("%2d  ", (y + 1)));
            for (int x = 0; x < size; x++) {
                // No tabuleiro de tiros, não exibimos navios escondidos 'S'
                char c = shotGrid[y][x];
                if (c == 'S') c = '.'; 
                rightRow.append(c).append(" ");
            }

            System.out.println(leftRow + "   |  " + rightRow);
        }
        System.out.println("Legenda: S = Navio | X = Acerto | o = Água | . = Desconhecido");
        System.out.println("=================================================================");
    }

    public String readInput(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
}