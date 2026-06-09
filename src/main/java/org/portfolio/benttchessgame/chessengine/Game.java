package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.listeners.BoardChangedListener;
import org.portfolio.benttchessgame.chessengine.listeners.StarterMovesAvailableListener;
import org.portfolio.benttchessgame.chessengine.listeners.TimeChangeListener;
import org.portfolio.benttchessgame.chessengine.listeners.TurnChangedListener;
import org.portfolio.benttchessgame.chessengine.players.Player;
import org.portfolio.benttchessgame.figures.Figure;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    PieceBoard currentPieceBoard;
    PieceBoardManager pieceBoardManager;
    Session currentSession;
    // Sessions will have data about time, special rules (en passant) available for players, move counter

    Figure[][] snapshot;

    Player p1, p2;

    boolean turn;

    Random random = new Random();

    List<BoardChangedListener> listeners = new ArrayList<>();

    List<StarterMovesAvailableListener> starterListeners = new ArrayList<>();

    public Game(Player player1, Player player2)
    {
        this.p1 = player1;
        this.p2 = player2;

        this.turn = random.nextBoolean(); //this only assigns the colors of the players, not the turn
        player1.setColor(turn);
        player2.setColor(!turn);

        this.currentPieceBoard = new PieceBoard();
        this.pieceBoardManager = new PieceBoardManager(currentPieceBoard);

        snapshot = currentPieceBoard.getSnapshot();

        // test code start
         this.currentSession = new Session();
         this.turn = currentSession.getTurn();
        // test code end

    }

    public PieceBoardManager getPieceBoardManager()
    {
        if(pieceBoardManager != null) return pieceBoardManager; else return null;
    }

    public Figure[][] getSnapshot()
    {
        return currentPieceBoard.getSnapshot();
    }

    public boolean[][] getStarterMoves()
    {
        return currentPieceBoard.getStarterMovesAvailable();
    }

    public void setStarterMoves(int row, int col, boolean val)
    {
        this.currentPieceBoard.getStarterMovesAvailable()[row][col] = val;
        notifyStarterMovesListeners();
    }

    public void move(MoveOperation moveOperation)
    {
        pieceBoardManager.move(moveOperation, turn);
        currentSession.switchTurn();
        this.turn = currentSession.getTurn();
        notifyListeners();
        notifyStarterMovesListeners();
    }

    public void notifyListeners()
    {
        for(BoardChangedListener listener : listeners)
        {
            listener.onBoardChanged(currentPieceBoard.getSnapshot());
        }
    }

    public void addBoardChangedListener(BoardChangedListener listener)
    {
        listeners.add(listener);
    }

    public void addTimeChangeListener(TimeChangeListener listener)
    {
        currentSession.getTimeListeners().add(listener);
    }

    public void addTurnListener(TurnChangedListener listener)
    {
        currentSession.getTurnListeners().add(listener);
    }

    public void addStarterMovesAvailableListener(StarterMovesAvailableListener listener)
    {
        starterListeners.add(listener);
    }

    public void notifyStarterMovesListeners()
    {
        for(StarterMovesAvailableListener listener : starterListeners)
        {
            listener.onStarterMovesAvailableChange(currentPieceBoard.getStarterMovesAvailable());
        }
    }
}
