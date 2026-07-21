package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.events.OnTurnChangedActivateFieldsEvent;
import org.portfolio.benttchessgame.chessengine.events.SetupFXPlayersEvent;
import org.portfolio.benttchessgame.chessengine.helperclasses.GameEventBus;
import org.portfolio.benttchessgame.chessengine.listeners.TimeChangeListener;
import org.portfolio.benttchessgame.chessengine.listeners.TurnChangedListener;
import org.portfolio.benttchessgame.chessengine.movement.MoveOperation;
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
    private final PlayerLinkedList playerList;

    Player player1, player2;
    private boolean inGame = true;

    private ConcurrentLinkedQueue<MoveOperation> operationQueue;
    private Game currentGame;

    private int turnCounter = 1;

    public Session(Player player1, Player player2)
    {
        this.player1 = player1;
        this.player2 = player2;

        Random random = new Random();
        player1.setColor(random.nextBoolean());
        player2.setColor(!player1.getColor());

        this.playerList = new PlayerLinkedList(player1, player2);
        if(playerList.head.getPlayer().getColor() != WHITE) playerList.next();

        new TimerThread(time, timeListeners);
     //   notifyFXPlayerSetup(player1, player2);

    }

    public boolean isInGame()
    {
        return inGame;
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
        //
        playerList.next();
        //
        turnCounter++;
        notifyTurnListeners();

       notifyFXPlayerSetup(player1, player2); //Test code

    }

    public Player getCurrentPlayer()
    {
        return playerList.getCurrentPlayer();
    }

    public void addTimeChangeListener(TimeChangeListener listener)
    {
        this.timeListeners.add(listener);
    }



    public void addTurnChangedListener(TurnChangedListener listener)
    {
        this.turnListeners.add(listener);
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

    public int getTurnCounter()
    {
        return this.turnCounter;
    }

    public void notifyFXPlayerSetup(Player p1, Player p2)
    {
        GameEventBus.getInstance().publishEvent(new SetupFXPlayersEvent(p1, p2));
    }
}
