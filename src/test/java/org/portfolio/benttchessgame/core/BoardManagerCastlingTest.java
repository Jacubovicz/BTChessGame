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

public class BoardManagerCastlingTest {

    private ChessBoard board;
    private GameState gameState;
    private BoardManager manager;
    private GameEventPublisher publisher;
    private King whiteKing;
    private Rook whiteRookK;
    private Rook whiteRookQ;

    @BeforeEach
    void setUp() {
        board = new ChessBoard();
        publisher = Mockito.mock(GameEventPublisher.class);
        gameState = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
        manager = new BoardManager(gameState, publisher);

        // Initialize AppContext
        GameEventBus eventBus = Mockito.mock(GameEventBus.class);
        ChessEngineWorker engineWorker = Mockito.mock(ChessEngineWorker.class);
        AppContext context = new AppContext(eventBus, engineWorker);
        AppContext.init(context);

        whiteKing = new King(PlayerColor.WHITE);
        whiteRookK = new Rook(PlayerColor.WHITE);
        whiteRookQ = new Rook(PlayerColor.WHITE);
        board.setPieceAt(0, 4, whiteKing);
        board.setPieceAt(0, 7, whiteRookK);
        board.setPieceAt(0, 0, whiteRookQ);
    }

    @Test
    void kingSideCastlingSucceedsWhenLegal() {
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(0, 4), new BoardSquare(0, 6))));
        assertEquals(whiteKing, board.getPieceAt(0, 6));
        assertEquals(whiteRookK, board.getPieceAt(0, 5));
    }

    @Test
    void queenSideCastlingSucceedsWhenLegal() {
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(0, 4), new BoardSquare(0, 2))));
        assertEquals(whiteKing, board.getPieceAt(0, 2));
        assertEquals(whiteRookQ, board.getPieceAt(0, 3));
    }

    @Test
    void cannotCastleIfKingHasMoved() {
        whiteKing.setDidMove();
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(0, 4), new BoardSquare(0, 6))));
    }

    @Test
    void cannotCastleIfRookHasMoved() {
        whiteRookK.setDidMove();
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(0, 4), new BoardSquare(0, 6))));
    }

    @Test
    void cannotCastleIfPiecesAreBetweenKingAndRook() {
        board.setPieceAt(0, 5, new Bishop(PlayerColor.WHITE));
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(0, 4), new BoardSquare(0, 6))));
    }

    @Test
    void cannotCastleWhileKingIsInCheck() {
        board.setPieceAt(5, 4, new Rook(PlayerColor.BLACK)); // Rook at (5, 4) checks (0, 4)
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(0, 4), new BoardSquare(0, 6))));
    }

    @Test
    void cannotCastleThroughAttackedSquare() {
        board.setPieceAt(5, 5, new Rook(PlayerColor.BLACK)); // Rook at (5, 5) checks (0, 5)
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(0, 4), new BoardSquare(0, 6))));
    }

    @Test
    void cannotCastleIntoCheck() {
        board.setPieceAt(5, 6, new Rook(PlayerColor.BLACK)); // Rook at (5, 6) checks (0, 6)
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(0, 4), new BoardSquare(0, 6))));
    }

    @Test
    void kingAndRookEndOnCorrectSquares() {
        manager.move(new Move(new BoardSquare(0, 4), new BoardSquare(0, 6)));
        assertEquals(whiteKing, board.getPieceAt(0, 6));
        assertEquals(whiteRookK, board.getPieceAt(0, 5));
        assertNull(board.getPieceAt(0, 4));
        assertNull(board.getPieceAt(0, 7));
    }
}
