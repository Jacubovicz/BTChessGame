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
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;
import org.portfolio.benttchessgame.piece.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BoardManagerKingTest {

    private final int KING_ROW = 0;
    private final int KING_COL = 4;

    ChessBoard board;
    BoardManager manager;
    GameEventPublisher publisher;
    GameState gameState;
    King king;

    @BeforeEach
    void setUp(){
        board = new ChessBoard();
        publisher = Mockito.mock(GameEventPublisher.class);
        gameState = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
        manager = new BoardManager(gameState, publisher);

        // Initialize AppContext
        GameEventBus eventBus = Mockito.mock(GameEventBus.class);
        ChessEngineWorker engineWorker = Mockito.mock(ChessEngineWorker.class);
        AppContext context = new AppContext(eventBus, engineWorker);
        AppContext.init(context);

        king = new King(PlayerColor.WHITE);
        board.setPieceAt(KING_ROW,KING_COL, king);
    }

    @Test
    void kingMovesOneSquare(){
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(KING_ROW, KING_COL), new BoardSquare(KING_ROW, KING_COL + 1))));
        assertEquals(board.getPieceAt(KING_ROW, KING_COL + 1), king);
        assertNull(board.getPieceAt(KING_ROW, KING_COL));
    }

    @Test
    void kingCannotMoveTwoSquaresNormally(){
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(KING_ROW, KING_COL), new BoardSquare(KING_ROW, KING_COL + 2))));
        assertEquals(board.getPieceAt(KING_ROW, KING_COL), king);
    }

    @Test
    void kingCapturesEnemyPiece(){
        Piece enemyPawn = new Pawn(PlayerColor.BLACK);
        board.setPieceAt(KING_ROW + 1, KING_COL, enemyPawn);
        
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(KING_ROW, KING_COL), new BoardSquare(KING_ROW + 1, KING_COL))));
        assertEquals(board.getPieceAt(KING_ROW + 1, KING_COL), king);
        assertNull(board.getPieceAt(KING_ROW, KING_COL));
    }

    @Test
    void kingCannotCaptureProtectedEnemyPiece(){
        Piece enemyPawn = new Pawn(PlayerColor.BLACK);
        board.setPieceAt(KING_ROW + 1, KING_COL, enemyPawn);
        // Protect the pawn with another enemy piece (e.g., another pawn)
        Piece protectingPawn = new Pawn(PlayerColor.BLACK, -1);
        board.setPieceAt(KING_ROW + 2, KING_COL + 1, protectingPawn); // This pawn at (2, 5) attacks (1, 4) if vector is -1

        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(KING_ROW, KING_COL), new BoardSquare(KING_ROW + 1, KING_COL))));
        assertEquals(board.getPieceAt(KING_ROW, KING_COL), king);
    }

    @Test
    void kingCannotMoveIntoCheck(){
        Piece enemyRook = new Rook(PlayerColor.BLACK);
        board.setPieceAt(KING_ROW + 5, KING_COL + 1, enemyRook); // Attacks the whole column KING_COL + 1
        
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(KING_ROW, KING_COL), new BoardSquare(KING_ROW, KING_COL + 1))));
        assertEquals(board.getPieceAt(KING_ROW, KING_COL), king);
    }

    @Test
    void kingsCannotBecomeAdjacent(){
        King enemyKing = new King(PlayerColor.BLACK);
        board.setPieceAt(KING_ROW + 2, KING_COL, enemyKing);
        
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(KING_ROW, KING_COL), new BoardSquare(KING_ROW + 1, KING_COL))));
        assertEquals(board.getPieceAt(KING_ROW, KING_COL), king);
    }

    @Test
    void kingCannotMoveOntoFriendlyPiece(){
        Piece friendlyPawn = new Pawn(PlayerColor.WHITE);
        board.setPieceAt(KING_ROW + 1, KING_COL, friendlyPawn);
        
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(KING_ROW, KING_COL), new BoardSquare(KING_ROW + 1, KING_COL))));
        assertEquals(board.getPieceAt(KING_ROW, KING_COL), king);
    }

}
