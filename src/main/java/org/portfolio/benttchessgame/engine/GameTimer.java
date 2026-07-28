package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.engine.event.TimerChangedEvent;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.listener.TimeChangeListener;
import java.util.List;

public class GameTimer extends Thread{

    private String time;
    List<TimeChangeListener> listeners;
    public GameTimer(String time, List<TimeChangeListener> listeners)
    {
        this.time = time;
        this.listeners = listeners;
        this.start();
    }

    @Override
    public void run() {

        try {
            for(int i = 0; i < 10000; i++)
            {
                Thread.sleep(1000);
                time = formatTime(i);
                System.out.print(time + " ");
                notifyTimerListeners();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String formatTime(int t)
    {
        StringBuilder str = new StringBuilder();

        int amountOfMins = t/60;

        if(amountOfMins>=1)
        {
            str.append(amountOfMins).append(" : ");
        }
        str.append(t - amountOfMins * 60);

        return str.toString();
    }

    public void notifyTimerListeners()
    {
        GameEventBus.getInstance().publishEvent(new TimerChangedEvent(time));
    }
}
