package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.listeners.BoardChangedListener;
import org.portfolio.benttchessgame.chessengine.listeners.StarterMovesAvailableListener;
import org.portfolio.benttchessgame.chessengine.listeners.TimeChangeListener;
import org.portfolio.benttchessgame.chessengine.listeners.TurnChangedListener;
import org.portfolio.benttchessgame.chessengine.players.Player;
import org.portfolio.benttchessgame.figures.Figure;
import org.portfolio.benttchessgame.figures.Pawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    PieceBoard currentPieceBoard;
    PieceBoardManager pieceBoardManager;

    Figure[][] snapshot;

    Player p1, p2;

    boolean turn;


    Random random = new Random();

    List<BoardChangedListener> listeners = new ArrayList<>();

    String time = "0";
    List<TimeChangeListener> timeListeners = new ArrayList<>();

    List<TurnChangedListener> turnListeners = new ArrayList<>();

    List<StarterMovesAvailableListener> starterListeners = new ArrayList<>();

    public Game(Player player1, Player player2)
    {
        this.p1 = player1;
        this.p2 = player2;

        this.turn = random.nextBoolean();
        player1.setColor(turn);
        player2.setColor(!turn);

        this.currentPieceBoard = new PieceBoard();
        this.pieceBoardManager = new PieceBoardManager(currentPieceBoard);

        snapshot = currentPieceBoard.getSnapshot();

        new TimerThread(time, timeListeners);

        setTurn(true);
    }

    public void setTurn(boolean turn)
    {
        this.turn = turn;

        notifyTurnListeners();
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
        setTurn(!turn);
        notifyListeners();
        notifyTurnListeners();
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
        timeListeners.add(listener);
    }

    public void addTurnListener(TurnChangedListener listener)
    {
        turnListeners.add(listener);
    }

    public void notifyTurnListeners()
    {
        for(TurnChangedListener listener : turnListeners)
        {
            listener.onTurnChanged(turn);
        }
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
