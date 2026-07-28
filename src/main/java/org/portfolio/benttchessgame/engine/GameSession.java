package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.engine.event.PlayersChangedEvent;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.listener.TimeChangeListener;
import org.portfolio.benttchessgame.engine.listener.TurnChangedListener;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.player.Player;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameSession {

    private final boolean WHITE = true;
    private final boolean DARK = false;


    private boolean turn = WHITE;

    private String time = "0";
    private List<TimeChangeListener> timeListeners = new ArrayList<>();
    private List<TurnChangedListener> turnListeners = new ArrayList<>();
    private final TurnOrder playerList;

    Player player1, player2;
    private boolean inGame = true;

    private ConcurrentLinkedQueue<Move> operationQueue;
    private ChessGame currentChessGame;

    private int turnCounter = 1;

    public GameSession(Player player1, Player player2)
    {
        this.player1 = player1;
        this.player2 = player2;

        Random random = new Random();
        player1.setColor(random.nextBoolean());
        player2.setColor(!player1.getColor());

        this.playerList = new TurnOrder(player1, player2);
        if(playerList.head.getPlayer().getColor() != WHITE) playerList.next();

        new GameTimer(time, timeListeners);
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
        GameEventBus.getInstance().publishEvent(new PlayersChangedEvent(p1, p2));
    }
}
