package org.portfolio.benttchessgame.engine.time;

import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.player.PlayerColor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TimeCounter extends Thread{

    private final long BASE_TIME_MILLIS;
    private final long INCREMENT_TIME_MILLIS; //to add time after the move
    private final long DELAY_MILLIS; //to start counting after this time
    private final boolean IS_INFINITE;

    private final long FIXED_TICK_TIME = 200; //to subtract time after each tick
    private final int NEGATIVE = -1;
    private final int POSITIVE = 1;
    private int FACTOR = NEGATIVE;

    PlayerColor currentPlayer;
    boolean isGameOn;

    private boolean isInfinite;

    private AtomicLong whiteTime;
    private AtomicLong blackTime;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> currentTimerTask;

   // private TimeEventPublisher timeEventPublisher;
    private final TimerData timerData;
    private final Runnable processAndSendToUI;

    public TimeCounter(TimeSettings timeSettings, TimerData timerData, Runnable processAndSendToUI){
        TimeSettings settings = timeSettings != null ? timeSettings : TimeSettings.standard();
        this.BASE_TIME_MILLIS = settings.baseTimeMillis();
        this.INCREMENT_TIME_MILLIS = settings.incrementMillis();
        this.DELAY_MILLIS = settings.delayMillis();
        this.IS_INFINITE = settings.isInfinite();//Don't know if I will find any use for it

        this.timerData = timerData;
        this.processAndSendToUI = processAndSendToUI;

        whiteTime = new AtomicLong(BASE_TIME_MILLIS);
        blackTime = new AtomicLong(BASE_TIME_MILLIS);

        this.currentPlayer = timerData != null ? timerData.currentPlayer() : PlayerColor.WHITE;
        this.isGameOn = false;

        if(settings.equals(TimeSettings.infinite())){
            isInfinite = true;
        }
    }

    public void startTurn(){
        if(!isGameOn){
            return;
        }

        AtomicLong activePlayerTime = currentPlayer == PlayerColor.WHITE ? whiteTime : blackTime;
        PlayerColor activePlayerColor = currentPlayer == PlayerColor.WHITE ? PlayerColor.WHITE : PlayerColor.BLACK;

        Runnable tickTask = () ->{
            if(!isGameOn){
                return;
            }
            if(isInfinite){
                FACTOR = POSITIVE;
            }

            long timeLeft = activePlayerTime.addAndGet(FIXED_TICK_TIME * FACTOR);

            if(timeLeft <= 0){
                endGame(activePlayerColor);
            }

            publishTimerDataChange(currentPlayer, timeLeft);
        };

        currentTimerTask = scheduler.scheduleAtFixedRate(tickTask, DELAY_MILLIS, FIXED_TICK_TIME, TimeUnit.MILLISECONDS);
    }

    public void changeTurn(){
        if(currentTimerTask != null){
            currentTimerTask.cancel(false);
        }

        AtomicLong activePlayerTime = currentPlayer == PlayerColor.WHITE ? whiteTime : blackTime;
        if(activePlayerTime.get() <= 0 && !isInfinite){
            PlayerColor localCurrentPlayer = currentPlayer == PlayerColor.WHITE ? PlayerColor.WHITE : PlayerColor.BLACK;
            endGame(localCurrentPlayer);
        }

        long newTime = activePlayerTime.addAndGet(INCREMENT_TIME_MILLIS);
        publishTimerDataChange(currentPlayer, newTime);
        //now the change with listener should be performed to the UI

        currentPlayer = currentPlayer.opposite();

        startTurn();
    }


    public void startGame(){
        System.out.println("Game started");
        isGameOn = true;
        startTurn();
    }

    public void stopTimer() {
        isGameOn = false;
        if (currentTimerTask != null) {
            currentTimerTask.cancel(true);
        }
    }

    public void endGame(PlayerColor playerColor){
        System.out.println(playerColor + " lost because of the time.");
        isGameOn = false;
        scheduler.shutdown();
        timerData.setTimerData(playerColor, 0, true);
        processAndSendToUI.run();
    }

    public void publishTimerDataChange(PlayerColor playerColor, long timeLeft){
        timerData.setTimerData(playerColor, timeLeft, isGameOn);
        processAndSendToUI.run();
    }

}
