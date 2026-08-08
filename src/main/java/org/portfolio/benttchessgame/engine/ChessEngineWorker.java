package org.portfolio.benttchessgame.engine;


import org.portfolio.benttchessgame.engine.command.GameCommand;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChessEngineWorker implements Runnable {

    private volatile boolean running = true;
    ChessEngine engine;

    private final int STANDARD_SIZE_QUEUE = 3;

    private Queue<GameCommand> commands = new ArrayBlockingQueue<>(STANDARD_SIZE_QUEUE);

    public ChessEngineWorker() {
        // this.engine = new ChessEngine(new OfflineBridgeImpl());
    }

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

       while(running)
       {
           if(!commands.isEmpty())
           {
               GameCommand command = commands.poll();
               if (command != null) {
                   command.execute(engine);
               }
           }
       }
    }
}
