package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

//nesta classe que tem as regras do jogo de xadrez partida de xadrex
		//nesta classe que vai dizer a dimens�o que vai ser 8 por 8
public class ChessMatch { 

	private Board board; //partida precisa do tabuleiro
	
	public ChessMatch() {
		board = new Board(8, 8);
		initialSetup();
	}
	
	public ChessPiece[][] getPieces(){  //retorna uma matriz de pe�as de xadrez corresponde essa partida
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()]; //quantidade de linha e colunas do tabuleiro
		for (int i=0 ; i < board.getRows(); i++) {
			for(int j=0 ; j < board.getColumns(); j++){
					mat[i][j] = (ChessPiece) board.piece(i,j);
			}	
		}return mat;
	}
	
	//9.3.1 movimento pe�a posi��o de origem = source pra target = destino
			//converter as duas posi��es para a matriz
			//validar a posi��o de origem
			//make move reposnsavel pelo movimento da pe�a
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toposition();
		Position target = targetPosition.toposition();
		validateSourcePosition(source);
	//12.3	
		validateSourcePosition(source, target);
		Piece capturedPiece = makeMove(source , target);
		return (ChessPiece)capturedPiece;
		
	}
	
	//9.3.2 Implemetar validateSourcePosition ; sen�o existir uma pe�a na posi��o de origem
	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}	//11.3 sen�o existe movientos possiveis n�o posso usar como origem
		if (!board.piece(position).isThereAnyPossibleMove() ) {
			throw new ChessException("There is no possible movers for the chosen pieces.");
			
		}
	}
	
	//9.3.3 Metodo make move pe�a se mover ;
		//remover a pe�a da posi��o de origem
		//remover a pe�a qe esteja na posi��o de destino
		//colocar a posi��o de origem na de destino
	private Piece makeMove(Position source , Position target) {
		Piece p = board.removePiece(source);
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		return capturedPiece;
	}
	
	//12.3 se para pe�a de origem a posi��o de destino nao � possivel n� posso mexer
	private void validateSourcePosition(Position source , Position target) {
		if(!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can t move to target possible");
		}
	}
	
	//7.3 metodo recebe as coordenadas do xadrez
		private void placeNewPiece(char column, int row, ChessPiece piece) {
			board.placePiece(piece,new ChessPosition(column, row).toposition());
		}

	
	//7.3 e 8.1.3 metodo responsavel por iniciar a partida de xadrez; lugar onde coloca as pe�as
	private void initialSetup( ) {
	        placeNewPiece('c', 1, new Rook(board, Color.white));
	        placeNewPiece('c', 2, new Rook(board, Color.white));
	        placeNewPiece('d', 2, new Rook(board, Color.white));
	        placeNewPiece('e', 2, new Rook(board, Color.white));
	        placeNewPiece('e', 1, new Rook(board, Color.white));
	        placeNewPiece('d', 1, new King(board, Color.white));
	      
	        placeNewPiece('c', 7, new Rook(board, Color.black));
	        placeNewPiece('c', 8, new Rook(board, Color.black));
	        placeNewPiece('d', 7, new Rook(board, Color.black));
	        placeNewPiece('e', 7, new Rook(board, Color.black));
	        placeNewPiece('e', 8, new Rook(board, Color.black));
	        placeNewPiece('d', 8, new King(board, Color.black));
	      
		}
	}
