package org.portfolio.benttchessgame.core;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;
import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.King;
import org.portfolio.benttchessgame.piece.Pawn;
import org.portfolio.benttchessgame.piece.Piece;
import org.portfolio.benttchessgame.piece.Queen;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardManagerTest {

    private GameEventBus eventBus;
    private AppContext appContext;

    @BeforeEach
    public void setUp() {
        eventBus = Mockito.mock(GameEventBus.class);
        ChessEngineWorker engineWorker = Mockito.mock(ChessEngineWorker.class);
        appContext = new AppContext(eventBus, engineWorker);
        AppContext.init(appContext);
    }

    /**
     * Tests for the proposeMoves method of BoardManager.
     * The proposeMoves method suggests legal moves for a piece
     * at a specific board square given the game state.
     */

    @Test
    public void testProposeMoves_NoPieceAtSquare_ReturnsEmptyList() {
        ChessBoard chessBoard = new ChessBoard();
        GameEventPublisher eventPublisher = Mockito.mock(GameEventPublisher.class);
        GameState gameState = new GameState(chessBoard, 0, PlayerColor.WHITE, new ArrayList<>());
        BoardManager boardManager = new BoardManager(gameState, eventPublisher);

        BoardSquare emptySquare = new BoardSquare(4, 4);

        List<BoardSquare> result = boardManager.getValidMoves(emptySquare);

        assertTrue(result.isEmpty(), "Expected an empty list when no piece is at the given square.");
    }

    @Test
    public void testProposeMoves_PieceAtSquare_ReturnsLegalMoves() {
        ChessBoard chessBoard = new ChessBoard();
        GameEventPublisher eventPublisher = Mockito.mock(GameEventPublisher.class);
        GameState gameState = new GameState(chessBoard, 0, PlayerColor.WHITE, new ArrayList<>());
        BoardManager boardManager = new BoardManager(gameState, eventPublisher);

        // Place a King to avoid IllegalArgumentException in MoveValidator
        chessBoard.setPieceAt(0, 0, new King(PlayerColor.WHITE));

        BoardSquare squareWithPawn = new BoardSquare(1, 4);
        Piece pawn = new Pawn(PlayerColor.WHITE);
        chessBoard.setPieceAt(1, 4, pawn);

        List<BoardSquare> result = boardManager.getValidMoves(squareWithPawn);

        // A White pawn at (1, 4) with vector 1 can move to (2, 4) and (3, 4)
        assertTrue(result.size() >= 2, "Expected at least two legal moves for the pawn.");
        assertTrue(result.contains(new BoardSquare(2, 4)), "Expected legal move to (2, 4).");
        assertTrue(result.contains(new BoardSquare(3, 4)), "Expected legal move to (3, 4).");
    }

    @Test
    public void testProposeMoves_PieceAtSquareNoLegalMoves_ReturnsEmptyList() {
        ChessBoard chessBoard = new ChessBoard();
        GameEventPublisher eventPublisher = Mockito.mock(GameEventPublisher.class);
        GameState gameState = new GameState(chessBoard, 0, PlayerColor.WHITE, new ArrayList<>());
        BoardManager boardManager = new BoardManager(gameState, eventPublisher);

        // Place a King to avoid IllegalArgumentException
        chessBoard.setPieceAt(0, 0, new King(PlayerColor.WHITE));

        BoardSquare squareWithBlockedPiece = new BoardSquare(1, 1);
        Piece pawn = new Pawn(PlayerColor.WHITE);
        chessBoard.setPieceAt(1, 1, pawn);
        
        // Block the pawn with another piece
        chessBoard.setPieceAt(2, 1, new Pawn(PlayerColor.WHITE));

        List<BoardSquare> result = boardManager.getValidMoves(squareWithBlockedPiece);

        assertTrue(result.isEmpty(), "Expected an empty list when the piece has no legal moves.");
    }

    @Test
    public void testProposeMoves_DifferentPieceTypes_ReturnsCorrectMoves() {
        ChessBoard chessBoard = new ChessBoard();
        GameEventPublisher eventPublisher = Mockito.mock(GameEventPublisher.class);
        GameState gameState = new GameState(chessBoard, 0, PlayerColor.WHITE, new ArrayList<>());
        BoardManager boardManager = new BoardManager(gameState, eventPublisher);

        // Place a King
        chessBoard.setPieceAt(0, 0, new King(PlayerColor.WHITE));

        BoardSquare squareWithQueen = new BoardSquare(3, 3);
        Piece queen = new Queen(PlayerColor.WHITE);
        chessBoard.setPieceAt(3, 3, queen);

        List<BoardSquare> result = boardManager.getValidMoves(squareWithQueen);

        // Queen at (3, 3) should have many moves. We check a few.
        assertFalse(result.isEmpty(), "Expected legal moves for the queen.");
        assertTrue(result.contains(new BoardSquare(2, 3)), "Expected legal move to (2, 3).");
        assertTrue(result.contains(new BoardSquare(3, 4)), "Expected legal move to (3, 4).");
        assertTrue(result.contains(new BoardSquare(4, 4)), "Expected legal move to (4, 4).");
    }
}