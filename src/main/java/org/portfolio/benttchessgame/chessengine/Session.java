package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.listeners.TimeChangeListener;
import org.portfolio.benttchessgame.chessengine.listeners.TurnChangedListener;
import org.portfolio.benttchessgame.chessengine.players.Player;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Session {

    private final boolean WHITE = true;
    private final boolean DARK = false;


    private boolean turn = WHITE;

    private String time = "0";
    private List<TimeChangeListener> timeListeners = new ArrayList<>();
    private List<TurnChangedListener> turnListeners = new ArrayList<>();
    Player player1, player2;
    private boolean inGame = true;

    private ConcurrentLinkedQueue<MoveOperation> operationQueue;
    private Game currentGame;

    private int turnCounter = 1;

    public Session()
    {
        new TimerThread(time, timeListeners);
    }

    public Session(Player player1, Player player2)
    {
        this.player1 = player1;
        this.player2 = player2;

        Random random = new Random();
        player1.setColor(random.nextBoolean());
        player2.setColor(!player1.getColor());

        new TimerThread(time, timeListeners);
        this.operationQueue = new ConcurrentLinkedQueue<>();
    }

    public boolean isInGame()
    {
        return inGame;
    }


    public void setCurrentGame(Game game)
    {
        this.currentGame = game;
        new MoveConsumerThread(this);
    }

    public Game getCurrentGame()
    {
        return this.currentGame;
    }

    public List<TimeChangeListener> getTimeListeners()
    {
        return timeListeners;
    }

    public boolean getTurn()
    {
        return turn;
    }

    public void switchTurn()
    {
        this.turn = !turn;
        turnCounter++;
        notifyTurnListeners();
    }

    public List<TurnChangedListener> getTurnListeners()
    {
        return turnListeners;
    }

    public void notifyTurnListeners()
    {
        for(TurnChangedListener listener : turnListeners)
        {
            listener.onTurnChanged(turn);
        }
    }

    public Player getPlayer1()
    {
        return this.player1;
    }

    public Player getPlayer2()
    {
        return this.player2;
    }

    public void setTurn(boolean color)
    {
        this.turn = color;
    }

    public void publishMoves(Queue<MoveOperation> operationQueue)
    {
        this.operationQueue.addAll(operationQueue);
    }

    public Queue<MoveOperation> getOperationQueue()
    {
        return this.operationQueue;
    }

    public int getTurnCounter()
    {
        return this.turnCounter;
    }
}
