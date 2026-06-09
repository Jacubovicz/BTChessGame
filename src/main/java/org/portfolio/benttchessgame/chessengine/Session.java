package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.listeners.TimeChangeListener;
import org.portfolio.benttchessgame.chessengine.listeners.TurnChangedListener;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private final boolean WHITE = true;
    private final boolean DARK = false;


    private boolean turn = WHITE;

    private String time = "0";
    private List<TimeChangeListener> timeListeners = new ArrayList<>();
    private List<TurnChangedListener> turnListeners = new ArrayList<>();

    public Session()
    {
        new TimerThread(time, timeListeners);
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
}
