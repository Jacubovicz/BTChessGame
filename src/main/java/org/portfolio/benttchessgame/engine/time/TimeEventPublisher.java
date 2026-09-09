package org.portfolio.benttchessgame.engine.time;

import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.event.TimeCounterEvent;
import org.portfolio.benttchessgame.engine.event.TimeRunOutEvent;
import org.portfolio.benttchessgame.engine.player.PlayerColor;


public class TimeEventPublisher {

    GameEventPublisher publisher;

    public TimeEventPublisher(GameEventPublisher publisher){
        this.publisher = publisher;
    }

    public void publishTimeEvent(PlayerColor playerColor, long timeLeft){
        publisher.publishEvent(new TimeCounterEvent(playerColor, timeLeft));
    }

    public void publishTimeRunOut(PlayerColor playerColor){
        publisher.publishEvent(new TimeRunOutEvent(playerColor));
    }
}
