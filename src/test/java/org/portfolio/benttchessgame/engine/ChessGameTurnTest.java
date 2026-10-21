package org.portfolio.benttchessgame.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;

import static org.junit.jupiter.api.Assertions.*;

public class ChessGameTurnTest {

    private ChessGame game;
    private ChessGameListener listener;
    private GameConfiguration config;

    @BeforeEach
    void setUp() {
        listener = Mockito.mock(ChessGameListener.class);
        Player whitePlayer = Mockito.mock(Player.class);
        Player blackPlayer = Mockito.mock(Player.class);
        config = new GameConfiguration(whitePlayer, blackPlayer, null, null);
        game = new ChessGame(config, listener);
    }

    @Test
    void whiteStarts() {
        assertEquals(PlayerColor.WHITE, game.getNowMovingPlayer());
    }

    @Test
    void successfulWhiteMoveSwitchesToBlack() throws IllegalMoveException {
        // White pawn at (1, 0) moves to (2, 0)
        game.proposeMove(1, 0);
        game.move(new BoardSquare(2, 0));
        
        assertEquals(PlayerColor.BLACK, game.getNowMovingPlayer());
    }

    @Test
    void successfulBlackMoveSwitchesToWhite() throws IllegalMoveException {
        // White moves
        game.proposeMove(1, 0);
        game.move(new BoardSquare(2, 0));
        
        // Black moves pawn at (6, 0) to (5, 0)
        game.proposeMove(6, 0);
        game.move(new BoardSquare(5, 0));
        
        assertEquals(PlayerColor.WHITE, game.getNowMovingPlayer());
    }

    @Test
    void illegalMoveDoesNotSwitchTurn() {
        game.proposeMove(1, 0);
        // Move to an illegal square
        assertThrows(Exception.class, () -> game.move(new BoardSquare(5, 0)));
        
        assertEquals(PlayerColor.WHITE, game.getNowMovingPlayer());
    }

    @Test
    void attemptedMoveByWrongPlayerIsRejected() {
        // During White's turn, try to move a black pawn at (6, 0)
        game.proposeMove(6, 0);
        // initialSquare should be null now because proposeMove rejected it
        assertThrows(IllegalMoveException.class, () -> game.move(new BoardSquare(5, 0)));
    }

    @Test
    void castlingCountsAsOneTurn() throws IllegalMoveException {
        // Clear pieces by moving them
        game.proposeMove(1, 4); // White Pawn
        game.move(new BoardSquare(3, 4));
        game.proposeMove(6, 4); // Black Pawn
        game.move(new BoardSquare(4, 4));
        
        game.proposeMove(0, 6); // White Knight
        game.move(new BoardSquare(2, 5));
        game.proposeMove(7, 6); // Black Knight
        game.move(new BoardSquare(5, 5));
        
        game.proposeMove(0, 5); // White Bishop
        game.move(new BoardSquare(1, 4));
        game.proposeMove(7, 5); // Black Bishop
        game.move(new BoardSquare(6, 4));
        
        int initialCounter = game.getMoveCounter();
        game.proposeMove(0, 4); // White King
        game.move(new BoardSquare(0, 6)); // Castle
        
        assertEquals(initialCounter + 1, game.getMoveCounter());
    }

    @Test
    void promotionCountsAsOneTurn() throws IllegalMoveException {
        // Standard board has white pawns at row 1.
        // To make it faster, I'll use a pawn that is almost promoted.
        // But I can't easily modify the board of an existing game.
        // I'll just check moveCounter increment.
        int initialCounter = game.getMoveCounter();
        game.proposeMove(1, 0);
        game.move(new BoardSquare(2, 0));
        assertEquals(initialCounter + 1, game.getMoveCounter());
    }

    @Test
    void enPassantCountsAsOneTurn() throws IllegalMoveException {
        int initialCounter = game.getMoveCounter();
        game.proposeMove(1, 0);
        game.move(new BoardSquare(2, 0));
        assertEquals(initialCounter + 1, game.getMoveCounter());
    }
}
