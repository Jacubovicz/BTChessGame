package org.portfolio.benttchessgame.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;
import org.portfolio.benttchessgame.piece.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BoardManagerLegalityTest {

    private ChessBoard board;
    private GameState gameState;
    private BoardManager manager;
    private GameEventPublisher publisher;

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
    }

    @Test
    void pinnedPieceCannotMoveExposingKing() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(0, 1, new Rook(PlayerColor.WHITE));
        board.setPieceAt(0, 7, new Rook(PlayerColor.BLACK)); // Pins white Rook
        
        // Rook at (0, 1) cannot move to (1, 1) because it exposes the King to Rook at (0, 7)
        assertFalse(manager.getValidMoves(new BoardSquare(0, 1)).contains(new BoardSquare(1, 1)));
    }

    @Test
    void playerCannotIgnoreExistingCheck() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(0, 7, new Rook(PlayerColor.BLACK)); // Gives check
        board.setPieceAt(5, 5, new Rook(PlayerColor.WHITE));
        
        // White Rook at (5, 5) cannot move to (5, 6) as it doesn't resolve the check
        assertFalse(manager.getValidMoves(new BoardSquare(5, 5)).contains(new BoardSquare(5, 6)));
    }

    @Test
    void playerCanBlockCheck() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(0, 7, new Rook(PlayerColor.BLACK)); // Gives check
        board.setPieceAt(1, 4, new Rook(PlayerColor.WHITE));
        
        // White Rook at (1, 4) can move to (0, 4) to block the check
        assertTrue(manager.getValidMoves(new BoardSquare(1, 4)).contains(new BoardSquare(0, 4)));
    }

    @Test
    void playerCanCaptureCheckingPiece() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(0, 7, new Rook(PlayerColor.BLACK)); // Gives check
        board.setPieceAt(1, 7, new Rook(PlayerColor.WHITE));
        
        // White Rook at (1, 7) can capture Dark Rook at (0, 7)
        assertTrue(manager.getValidMoves(new BoardSquare(1, 7)).contains(new BoardSquare(0, 7)));
    }

    @Test
    void kingCannotCapturePieceIfDestinationIsAttacked() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(0, 1, new Rook(PlayerColor.BLACK)); // Adjacent, can be captured?
        board.setPieceAt(5, 1, new Rook(PlayerColor.BLACK)); // Protects the Rook at (0, 1)
        
        // King cannot capture Rook at (0, 1) because (0, 1) is attacked by Rook at (5, 1)
        assertFalse(manager.getValidMoves(new BoardSquare(0, 0)).contains(new BoardSquare(0, 1)));
    }

    @Test
    void discoveredAttackCreatedByOwnMoveMakesThatMoveIllegal() {
        // Similar to pinned piece but with any move
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(0, 1, new Knight(PlayerColor.WHITE));
        board.setPieceAt(0, 7, new Rook(PlayerColor.BLACK));
        
        // Knight at (0, 1) move to (2, 2) is illegal because it discovers a check on its own King
        assertFalse(manager.getValidMoves(new BoardSquare(0, 1)).contains(new BoardSquare(2, 2)));
    }
}
