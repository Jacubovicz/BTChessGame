package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.listeners.BoardChangedListener;
import org.portfolio.benttchessgame.chessengine.listeners.StarterMovesAvailableListener;
import org.portfolio.benttchessgame.chessengine.listeners.TimeChangeListener;
import org.portfolio.benttchessgame.chessengine.listeners.TurnChangedListener;
import org.portfolio.benttchessgame.chessengine.players.Player;
import org.portfolio.benttchessgame.figures.Figure;


import java.util.ArrayList;
import java.util.List;

public class Game {

    PieceBoard currentPieceBoard;
    PieceBoardManager pieceBoardManager;
    Session currentSession;
    // Sessions will have data about time, special rules (en passant) available for players, move counter



    Figure[][] snapshot;

    Player p1, p2;

    boolean turn;

    List<BoardChangedListener> listeners = new ArrayList<>();

    List<StarterMovesAvailableListener> starterListeners = new ArrayList<>();


    public Game(Session gameSession)
    {
        this.p1 = gameSession.getPlayer1();
        this.p2 = gameSession.getPlayer2();

        this.currentPieceBoard = new PieceBoard();
        this.pieceBoardManager = new PieceBoardManager(currentPieceBoard);

        snapshot = currentPieceBoard.getSnapshot();

         this.currentSession = gameSession;
         this.turn = currentSession.getTurn();
       //  new MoveConsumerThread(this.p1, this.p2, this);
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

    /*
    The switchTurn parameter in here is responsible in notifying the system if the turn
    should have been changed after the current movement.
     */
    public void move(MoveOperation moveOperation, boolean switchTurn)
    {
        pieceBoardManager.move(moveOperation, this.currentSession);
        if(switchTurn)
        {
            currentSession.switchTurn();
            this.turn = currentSession.getTurn();
            pieceBoardManager.fillTheStarterMoves(turn);
        }
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
