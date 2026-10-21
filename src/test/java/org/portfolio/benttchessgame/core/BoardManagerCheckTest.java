package org.portfolio.benttchessgame.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.rule.MoveValidator;
import org.portfolio.benttchessgame.engine.rule.ChessRules;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;
import org.portfolio.benttchessgame.piece.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BoardManagerCheckTest {

    private ChessBoard board;
    private GameState gameState;
    //private BoardManager manager;
    //private GameEventPublisher publisher;

    @BeforeEach
    void setUp() {
        board = new ChessBoard();
        //publisher = Mockito.mock(GameEventPublisher.class);
        gameState = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
      //  manager = new BoardManager(gameState, publisher);

        // Initialize AppContext
        GameEventBus eventBus = Mockito.mock(GameEventBus.class);
        ChessEngineWorker engineWorker = Mockito.mock(ChessEngineWorker.class);
        AppContext context = new AppContext(eventBus, engineWorker);
        AppContext.init(context);
    }

    @Test
    void rookGivesCheck() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(0, 7, new Rook(PlayerColor.BLACK));
        assertTrue(MoveValidator.doesGenerateCheckDangerForPlayer(board, PlayerColor.WHITE));
    }

    @Test
    void bishopGivesCheck() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(7, 7, new Bishop(PlayerColor.BLACK));
        assertTrue(MoveValidator.doesGenerateCheckDangerForPlayer(board, PlayerColor.WHITE));
    }

    @Test
    void queenGivesCheck() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(0, 7, new Queen(PlayerColor.BLACK));
        assertTrue(MoveValidator.doesGenerateCheckDangerForPlayer(board, PlayerColor.WHITE));
    }

    @Test
    void knightGivesCheck() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(1, 2, new Knight(PlayerColor.BLACK));
        assertTrue(MoveValidator.doesGenerateCheckDangerForPlayer(board, PlayerColor.WHITE));
    }

    @Test
    void pawnGivesCheck() {
        board.setPieceAt(0, 1, new King(PlayerColor.WHITE));
        board.setPieceAt(1, 0, new Pawn(PlayerColor.BLACK, -1)); // Dark pawn moves from 1 to 0? No, vector -1 means from 1 to 0 is forward.
        // Beating offset for vector -1 is {{-1,-1}, {-1,1}}. 
        // If pawn is at (1, 0), beating is (1-1, 0-1) = (0, -1) and (1-1, 0+1) = (0, 1).
        // (0, 1) is where the King is.
        assertTrue(MoveValidator.doesGenerateCheckDangerForPlayer(board, PlayerColor.WHITE));
    }

    @Test
    void kingGivesAttack() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(1, 1, new King(PlayerColor.BLACK));
        assertTrue(MoveValidator.doesGenerateCheckDangerForPlayer(board, PlayerColor.WHITE));
    }

    @Test
    void blockedSlidingPieceDoesNotGiveCheck() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(0, 1, new Pawn(PlayerColor.WHITE));
        board.setPieceAt(0, 7, new Rook(PlayerColor.BLACK));
        assertFalse(MoveValidator.doesGenerateCheckDangerForPlayer(board, PlayerColor.WHITE));
    }

    @Test
    void unrelatedEnemyPieceDoesNotCauseFalseCheck() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(5, 5, new Rook(PlayerColor.BLACK));
        assertFalse(MoveValidator.doesGenerateCheckDangerForPlayer(board, PlayerColor.WHITE));
    }

    @Test
    void actualCheckmateReturnsTrue() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(0, 7, new Rook(PlayerColor.BLACK));
        board.setPieceAt(1, 7, new Rook(PlayerColor.BLACK));
        // King is at (0, 0). (0, 7) Rook checks row 0. (1, 7) Rook checks row 1.
        // King can't move to row 1. Can't move in row 0.
        assertTrue(ChessRules.isCheckmate(gameState, PlayerColor.WHITE));
    }

    @Test
    void kingCanEscapeReturnsFalse() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(0, 7, new Rook(PlayerColor.BLACK));
        // King at (0, 0) can move to (1, 0) or (1, 1)
        assertFalse(ChessRules.isCheckmate(gameState, PlayerColor.WHITE));
    }

    @Test
    void checkingPieceCanBeCapturedReturnsFalse() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(1, 1, new Rook(PlayerColor.BLACK)); // Rook gives check
        board.setPieceAt(2, 2, new Bishop(PlayerColor.WHITE)); // Bishop can capture Rook
        assertFalse(ChessRules.isCheckmate(gameState, PlayerColor.WHITE));
    }

    @Test
    void checkCanBeBlockedReturnsFalse() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(0, 7, new Rook(PlayerColor.BLACK)); // Rook gives check on row 0
        board.setPieceAt(1, 4, new Rook(PlayerColor.WHITE)); // White Rook at (1, 4) can move to (0, 4) to block
        assertFalse(ChessRules.isCheckmate(gameState, PlayerColor.WHITE));
    }

    @Test
    void stalemateIsNotCheckmate() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(1, 2, new Rook(PlayerColor.BLACK));
        board.setPieceAt(2, 1, new Rook(PlayerColor.BLACK));
        // King is at (0, 0). Not in check.
        // Squares (0, 1), (1, 0), (1, 1) are attacked.
        // No legal moves for King, but NOT in check.
        assertFalse(ChessRules.isCheckmate(gameState, PlayerColor.WHITE));
    }

    @Test
    void kingIsNotInCheckReturnsFalse() {
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        assertFalse(ChessRules.isCheckmate(gameState, PlayerColor.WHITE));
    }
}
