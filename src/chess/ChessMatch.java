package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

//nesta classe que tem as regras do jogo de xadrez partida de xadrex
		//nesta classe que vai dizer a dimens�o que vai ser 8 por 8
public class ChessMatch { 

//15.1.1 criar atibutos turn current player e seus get seter	
	private int turn;
	private Color currentPlayer;
	private Board board; //partida precisa do tabuleiro
	private boolean check;  //17.2.2
	private boolean checkMate; //18.1.1
	private ChessPiece enPassantVulnerable; //25.1.1
	private ChessPiece promoted; //26.1
	
//16.4.1
	private List <Piece> piecesOnTheBoard = new ArrayList<>();
	private List <Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.white;
		initialSetup();
	}
	
	
	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
	}

	public ChessPiece[][] getPieces(){  //retorna uma matriz de pe�as de xadrez corresponde essa partida
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()]; //quantidade de linha e colunas do tabuleiro
		for (int i=0 ; i < board.getRows(); i++) {
			for(int j=0 ; j < board.getColumns(); j++){
					mat[i][j] = (ChessPiece) board.piece(i,j);
			}	
		}return mat;
	}
	
	//13.1 aplica��o colorir imprimir as posi��es possiveis de uma posi��o de origem
	public boolean[][] possibleMoves(ChessPosition sourcePosition){
		Position position = sourcePosition.toposition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
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
		
	//17.2.6
		if (testCheck(currentPlayer)) {
			UndoMove(source , target , capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
	//25.1.2	
		ChessPiece movedPiece = (ChessPiece)board.piece(target);
		
	//26.2.1 specialmove promotion
		promoted = null;
		if (movedPiece instanceof Pawn) {
			if ((movedPiece.getColor() == Color.white && target.getRow() == 0) || (movedPiece.getColor() == Color.black && target.getRow() == 7)) {
				promoted = (ChessPiece)board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}
		
		
		check = (testCheck(opponent(currentPlayer))) ? true : false ;
	//18.1.3	
		if(testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
	}
	else {
		nextTurn();
	}
		
	//25.3 special move en passant
		if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		}
		else {
			enPassantVulnerable = null;
		}
		
		return (ChessPiece)capturedPiece;
	}
	public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if (!type.equals("B") && !type.equals("N") && !type.equals("R") & !type.equals("Q")) {
			return promoted;
		}
		
		Position pos = promoted.getChessPosition().toposition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color) {
		if (type.equals("B")) return new Bishop(board, color);
		if (type.equals("N")) return new Knight(board, color);
		if (type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
	}
		
	
	
	//9.3.2 Implemetar validateSourcePosition ; sen�o existir uma pe�a na posi��o de origem
	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}	
			//15.1.2
		if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}
			
			//11.3 sen�o existe movientos possiveis n�o posso usar como origem
		if (!board.piece(position).isThereAnyPossibleMove() ) {
			throw new ChessException("There is no possible movers for the chosen pieces.");
			
		}
	}
	
	//9.3.3 Metodo make move pe�a se mover ;
		//remover a pe�a da posi��o de origem
		//remover a pe�a qe esteja na posi��o de destino
		//colocar a posi��o de origem na de destino
	private Piece makeMove(Position source , Position target) {
	//19.2
		ChessPiece p = (ChessPiece)board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
	//16.4.3	
		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		
	//24.4 make move castling king	
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}

		//24.5 #specialmove castling queenside rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}	
		
		//25.3 #specialmove en passant
		if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				if (p.getColor() == Color.white) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				}
				else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = board.removePiece(pawnPosition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
		}
		
		return capturedPiece;
	}
	
	//17.2.1
	private void UndoMove(Position source , Position target , Piece capturedPiece) {
		//19.2
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		
		//24.6 make move castling king	inverso
				if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
					Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
					Position targetT = new Position(source.getRow(), source.getColumn() + 1);
					ChessPiece rook = (ChessPiece)board.removePiece(targetT);
					board.placePiece(rook, sourceT);
					rook.decreaseMoveCount();
				}

		//24.7 #specialmove castling queenside rook inverso
				if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
					Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
					Position targetT = new Position(source.getRow(), source.getColumn() - 1);
					ChessPiece rook = (ChessPiece)board.removePiece(targetT);
					board.placePiece(rook, sourceT);
					rook.decreaseMoveCount();
				}	
		
		//25.4 enpassant Undomove
				if (p instanceof Pawn) {
					if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
						ChessPiece pawn =(ChessPiece)board.removePiece(target);
						Position pawnPosition;
						if (p.getColor() == Color.white) {
							pawnPosition = new Position(3, target.getColumn());
						}
						else {
							pawnPosition = new Position(4, target.getColumn());
						}
						board.placePiece(pawn, pawnPosition);
					}
	
				}
			
	}
	
	//12.3 se para pe�a de origem a posi��o de destino nao � possivel n� posso mexer
	private void validateSourcePosition(Position source , Position target) {
		if(!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can t move to target possible");
		}
	}
	
	//15.1.2 mETODO NEXT TURN
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.white) ? Color.black : Color.white;
	}
	
	//17.2.3
		private Color opponent(Color color) {
			return (color == Color.white) ? Color.black : Color.white;
		}
		
		private ChessPiece King(Color color) {
			List<Piece> list = piecesOnTheBoard.stream().filter(x->((ChessPiece)x).getColor() == color).collect(Collectors.toList());
			for (Piece p : list) {
				if (p instanceof King) {
					return (ChessPiece) p;
				}
			}
			throw new IllegalStateException("There is no " + color + "king on the board");
		}
		
	//17.2.5 Test check
		private boolean testCheck(Color color) {
			Position kingPosition = King(color).getChessPosition().toposition();
			List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x->((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
			for (Piece p : opponentPieces ) {
				boolean[][] mat= p.possibleMoves();
				if(mat[kingPosition.getRow()][kingPosition.getColumn()])  {
					return true;
				}
			}
			return false;
		
		}
		
	//18.1.2
		private boolean testCheckMate(Color color) {
			if(!testCheck(color)) {
				return false;
			}
			List<Piece> list = piecesOnTheBoard.stream().filter(x->((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
			for(Piece p : list) {
				boolean[][] mat = p.possibleMoves();
				for (int i=0; i<board.getRows(); i++) {
					for(int j=0; j<board.getColumns(); j++) {
						if (mat[i][j]) {
							Position source = ((ChessPiece)p).getChessPosition().toposition();
							Position target = new Position (i, j);
							Piece capturedPiece = makeMove(source, target);
							boolean testCheck = testCheck(color);
							UndoMove(source, target, capturedPiece);
							if (!testCheck) {
								return false;
							}
							
						}
					}
				}
			}
			return true;
		}
		
	//7.3 metodo recebe as coordenadas do xadrez
		private void placeNewPiece(char column, int row, ChessPiece piece) {
			board.placePiece(piece,new ChessPosition(column, row).toposition());
			piecesOnTheBoard.add(piece);   //16.4.2
		}

	
	//7.3 e 8.1.3 metodo responsavel por iniciar a partida de xadrez; lugar onde coloca as pe�as
	private void initialSetup( ) {
		
		//20.2 colocar posi��es 
		
		
        
		placeNewPiece('a', 1, new Rook(board, Color.white));
		placeNewPiece('b', 1, new Knight(board, Color.white));
		placeNewPiece('c', 1, new Bishop(board, Color.white));
		placeNewPiece('d', 1, new Queen(board, Color.white));
        placeNewPiece('e', 1, new King(board, Color.white, this));
        placeNewPiece('f', 1, new Bishop(board, Color.white));
        placeNewPiece('g', 1, new Knight(board, Color.white));
        placeNewPiece('h', 1, new Rook(board, Color.white));
        placeNewPiece('a', 2, new Pawn(board, Color.white,this));
        placeNewPiece('b', 2, new Pawn(board, Color.white,this));
        placeNewPiece('c', 2, new Pawn(board, Color.white,this));
        placeNewPiece('d', 2, new Pawn(board, Color.white,this));
        placeNewPiece('e', 2, new Pawn(board, Color.white,this));
        placeNewPiece('f', 2, new Pawn(board, Color.white,this));
        placeNewPiece('g', 2, new Pawn(board, Color.white,this));
        placeNewPiece('h', 2, new Pawn(board, Color.white,this));
        
       
        
        placeNewPiece('a', 8, new Rook(board, Color.black));
        placeNewPiece('b', 8, new Knight(board, Color.black));
        placeNewPiece('c', 8, new Bishop(board, Color.black));
        placeNewPiece('d', 8, new Queen(board, Color.black));
        placeNewPiece('e', 8, new King(board, Color.black, this));
        placeNewPiece('f', 8, new Bishop(board, Color.black));
        placeNewPiece('g', 8, new Knight(board, Color.black));
        placeNewPiece('h', 8, new Rook(board, Color.black));
        placeNewPiece('a', 7, new Pawn(board, Color.black,this));
        placeNewPiece('b', 7, new Pawn(board, Color.black,this));
        placeNewPiece('c', 7, new Pawn(board, Color.black,this));
        placeNewPiece('d', 7, new Pawn(board, Color.black,this));
        placeNewPiece('e', 7, new Pawn(board, Color.black,this));
        placeNewPiece('f', 7, new Pawn(board, Color.black,this));
        placeNewPiece('g', 7, new Pawn(board, Color.black,this));
        placeNewPiece('h', 7, new Pawn(board, Color.black,this));
	    
	         
	      
		}
	}

