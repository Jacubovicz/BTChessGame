package org.portfolio.benttchessgame.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.King;
import org.portfolio.benttchessgame.piece.Pawn;
import org.portfolio.benttchessgame.piece.Queen;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BoardManagerPawnTest {

    ChessBoard board;
    GameEventPublisher publisher;

    @BeforeEach
    public void setup(){
        board = new ChessBoard();
        publisher = Mockito.mock(GameEventPublisher.class);
    }

    @Test
    void WhitePawnMovesOneSquareForwardFromNormalPosition(){

        board.setupTheBoard();
        GameState state = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
        BoardManager boardManager = new BoardManager(state, publisher);

        assertInstanceOf(Pawn.class, board.getPieceAt(1,4));

        Pawn p =(Pawn)board.getPieceAt(1,4);

        Move move = new Move(new BoardSquare(1,4), new BoardSquare(2,4));

        boardManager.move(move);

        assertEquals(board.getPieceAt(2,4), p);
        assertNull(board.getPieceAt(1,4), "There shouldn't be anything left on that position");
    }

    @Test
    void BlackPawnMovesOneSquareForward(){
        board.setupTheBoard();
        GameState state = new GameState(board, 0, PlayerColor.BLACK, new ArrayList<>());
        BoardManager boardManager = new BoardManager(state, publisher);

        assertInstanceOf(Pawn.class, board.getPieceAt(6,4));

        Pawn blackPawn = (Pawn) board.getPieceAt(6,4);

        Move move = new Move(new BoardSquare(6,4), new BoardSquare(5,4));

        boardManager.move(move);

        assertEquals(board.getPieceAt(5,4), blackPawn);
        assertNull(board.getPieceAt(6,4));
    }

    @Test
    void PawnMovesTwoSquaresFromStartingRank(){

        board.setupTheBoard();
        GameState state = new GameState(board, 0, PlayerColor.BLACK, new ArrayList<>());
        BoardManager boardManager = new BoardManager(state, publisher);

        assertInstanceOf(Pawn.class, board.getPieceAt(6,4));

        Pawn blackPawn = (Pawn) board.getPieceAt(6,4);

        Move move = new Move(new BoardSquare(6,4), new BoardSquare(4,4));

        boardManager.move(move);

        assertEquals(board.getPieceAt(4,4), blackPawn);
        assertNull(board.getPieceAt(5,4));
        assertNull(board.getPieceAt(6,4));
    }

    @Test
    void PawnCannotMoveTwoSquaresAfterItHasMoved(){
        board.setupTheBoard();
        GameState state = new GameState(board, 0, PlayerColor.BLACK, new ArrayList<>());
        BoardManager boardManager = new BoardManager(state, publisher);
        assertInstanceOf(Pawn.class, board.getPieceAt(1,4));

        Pawn pawn = (Pawn) board.getPieceAt(1,4);
        pawn.disableFirstMove();

        Move move = new Move(new BoardSquare(1,4), new BoardSquare(3,4));

        assertThrows(IllegalArgumentException.class, () -> boardManager.move(move));
    }

    @Test
    void PawnCannotMoveForwardIfSquareIsOccupied(){
        board.setupTheBoard();
        GameState state = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
        BoardManager boardManager = new BoardManager(state, publisher);
        assertInstanceOf(Pawn.class, board.getPieceAt(1,4));

        Queen queen = new Queen(PlayerColor.WHITE);
        board.setPieceAt(2,4,queen);

        assertEquals(new ArrayList<>(), boardManager.getValidMoves(new BoardSquare(1,4)));
    }

    @Test
    void PawnCannotMoveTwoSquaresIfIntermediateSquareIsOccupied(){
        board.setupTheBoard();
        GameState state = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
        BoardManager boardManager = new BoardManager(state, publisher);
        assertInstanceOf(Pawn.class, board.getPieceAt(1,4));

        Queen queen = new Queen(PlayerColor.WHITE);
        board.setPieceAt(3,4,queen);

        Move move = new Move(new BoardSquare(1,4), new BoardSquare(3,4));
        assertThrows(IllegalArgumentException.class, () -> boardManager.move(move));
    }

    @Test
    void PawnCapturesDiagonally(){
        board.setPieceAt(2,3,new Pawn(PlayerColor.WHITE));
        board.setPieceAt(3,4,new Pawn(PlayerColor.BLACK));

        board.setPieceAt(0,7, new King(PlayerColor.WHITE));
        board.setPieceAt(7,0, new King(PlayerColor.BLACK));

        GameState state = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
        BoardManager boardManager = new BoardManager(state, publisher);
        Move move = new Move(new BoardSquare(2,3), new BoardSquare(3,4));
        assertDoesNotThrow(() -> boardManager.move(move));
    }

    @Test
    void PawnCannotCaptureForward(){
        board.setPieceAt(2,3,new Pawn(PlayerColor.WHITE));
        board.setPieceAt(3,3,new Pawn(PlayerColor.BLACK));

        board.setPieceAt(0,7, new King(PlayerColor.WHITE));
        board.setPieceAt(7,0, new King(PlayerColor.BLACK));

        GameState state = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
        BoardManager boardManager = new BoardManager(state, publisher);
        Move move = new Move(new BoardSquare(2,3), new BoardSquare(3,3));
        assertThrows(IllegalArgumentException.class, () -> boardManager.move(move));
    }

    @Test
    void PawnCannotMoveDiagonallyWithoutCapture(){
        board.setPieceAt(2,3,new Pawn(PlayerColor.WHITE));

        board.setPieceAt(0,7, new King(PlayerColor.WHITE));
        board.setPieceAt(7,0, new King(PlayerColor.BLACK));

        GameState state = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
        BoardManager boardManager = new BoardManager(state, publisher);
        Move move = new Move(new BoardSquare(2,3), new BoardSquare(3,2));
        assertThrows(IllegalArgumentException.class, () -> boardManager.move(move));
    }

    @Test
    void PawnCannotMoveBackwards(){
        board.setPieceAt(2,3,new Pawn(PlayerColor.WHITE));
        board.setPieceAt(0,7, new King(PlayerColor.WHITE));
        board.setPieceAt(7,0, new King(PlayerColor.BLACK));

        GameState state = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
        BoardManager boardManager = new BoardManager(state, publisher);
        Move move = new Move(new BoardSquare(2,3), new BoardSquare(1,3));
        assertThrows(IllegalArgumentException.class, () -> boardManager.move(move));
    }
}
