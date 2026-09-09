package org.portfolio.benttchessgame.engine.time;

import org.portfolio.benttchessgame.engine.player.PlayerColor;

public class TimerData {
    volatile private PlayerColor playerColor;
    volatile private long timeLeft;
    volatile private boolean isInGame;

    public TimerData(PlayerColor color){
        this.playerColor = color;
    }

    public void setTimerData(PlayerColor playerColor, long timeLeft, boolean isInGame){
        this.playerColor = playerColor;
        this.timeLeft = timeLeft;
        this.isInGame = isInGame;
    }

    public PlayerColor currentPlayer(){
        return this.playerColor;
    }

    public long timeLeft(){
        return this.timeLeft;
    }

    public boolean timeRanOut(){
        return !this.isInGame;
    }
}
