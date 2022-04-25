package application;

import java.util.InputMismatchException;
import java.util.Scanner;

import chess.ChessPiece;
import chess.ChessPosition;
import chess.Color;

public class UI {
//8.1.1 color piece codigos especiais , cores texto e fundo
	// https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
	
//10 codigo limpar tela do gitbash 
	// https://stackoverflow.com/questions/2979383/java-clear-the-console
	//Metodo abaixo esta no link acima
	public static void clearScreen() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	
//09.2 read chessposition fazer leitura posi��o com scanner
	public static  ChessPosition readChessPosition(Scanner sc) {
		try {
			String s = sc.nextLine();
			char column = s.charAt(0);
			int row = Integer.parseInt(s.substring(1));
			return new ChessPosition(column, row);
		} catch (RuntimeException e) {
			throw new InputMismatchException("Erro chees position , Valid  A1 at H8");
		}
	}
	
	
/*Imprimindo o tabuleiro
 * fazer 2 for um percorre as linhas e outr as colunas */
	
	public static void printBoard(ChessPiece[][] pieces) {
		for (int i=0; i<pieces.length; i++) {
			System.out.print((8-i) + " ");      //imprimir na tabela 8 a 1 linhas
			for (int j=0; j<pieces.length; j++) {
				printPiece(pieces[i][j]);        //colocar a pe�a na posi��o i e j
			}
			System.out.println();  //quebra linha do tabuleiro
		}
		System.out.println("  a b c d e f g h");  //imprimindo as colunas
	}
	
	
	/* 08.1.1 testar se a pe�a � branca ou preta
	 * 04 e  Metodo auxliar para imprimir uma unica pe�a   
	        -printpiece rebendo uma chessprice 
	        -se a pe�a for igual a nulo quer dizer n�o tem pe�a -
	        -caso contrario imprimo pe�a
	        - espa�o em branco pe�as nao colarem */
	
	private static void printPiece(ChessPiece piece) {
    	if (piece == null) {
            System.out.print("-");
        }
        else {
            if (piece.getColor() == Color.white) {
                System.out.print(ANSI_WHITE + piece + ANSI_RESET);
            }
            else {
                System.out.print(ANSI_YELLOW + piece + ANSI_RESET);
            }
        }
        System.out.print(" ");
	}
	
}
