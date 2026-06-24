package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.listeners.*;
import org.portfolio.benttchessgame.chessengine.players.Player;
import org.portfolio.benttchessgame.figures.Figure;

public class Game {

    private PieceBoardManager pieceBoardManager = null;
    private Session currentSession = null; // Sessions will have data about time, special rules (en passant) available for players, move counter
    private Player p1, p2;

    boolean turn;

    public Game(Session session)
    {
        this.p1 = session.getPlayer1();
        this.p2 = session.getPlayer2();

        this.currentSession = session;
        this.pieceBoardManager = new PieceBoardManager(new PieceBoard(), currentSession);
        this.turn = currentSession.getTurn();

        new MoveConsumerThread(currentSession, pieceBoardManager);
    }

    public Game(Player p1, Player p2)
    {
        this.p1 = p1;
        this.p2 = p2;

        this.currentSession = new Session(p1,p2);
        this.pieceBoardManager = new PieceBoardManager(new PieceBoard(), currentSession);
        this.turn = currentSession.getTurn();

        new MoveConsumerThread(currentSession, pieceBoardManager);
    }

    public Figure[][] getSnapshot()
    {
        return pieceBoardManager.getSnapshot().getPiecesOnBoard();
    }

    public void fillTheStarterMoves()
    {
        pieceBoardManager.fillTheStarterMoves(currentSession.getTurn());
    }

    public void proposeMove(int row, int col)
    {
       this.pieceBoardManager.proposeMove(row, col, currentSession);
    }

    public void move(MoveOperation operation)
    {
        pieceBoardManager.move(operation, currentSession, false);
    }

    public void resetTheGame(){}
}
