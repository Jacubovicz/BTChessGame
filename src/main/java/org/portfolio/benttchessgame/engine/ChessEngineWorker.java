package org.portfolio.benttchessgame.engine;


import org.portfolio.benttchessgame.engine.command.GameCommand;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChessEngineWorker implements Runnable {

    ChessEngine engine;
    private volatile boolean running = true;

    private final int STANDARD_SIZE_QUEUE = 20;

    private BlockingQueue<GameCommand> commands = new ArrayBlockingQueue<>(STANDARD_SIZE_QUEUE);

    public void setEngine(ChessEngine engine)
    {
        this.engine = engine;
    }

   public void post(GameCommand command)
   {
       this.commands.offer(command);
   }

    @Override
    public void run() {
               try {
                   while(running && !Thread.currentThread().isInterrupted()){
                       GameCommand command = commands.take();
                       if(engine != null){
                           command.execute(engine);
                       }
                   }
               } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
               } catch (IllegalMoveException e) {
                   throw new RuntimeException(e);
               }
    }
}
