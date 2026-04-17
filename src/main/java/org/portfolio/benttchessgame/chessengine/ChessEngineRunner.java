package org.portfolio.benttchessgame.chessengine;


import org.portfolio.benttchessgame.chessengine.commands.EngineCommand;
import org.portfolio.benttchessgame.chessengine.commands.StartNewGame;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChessEngineRunner implements Runnable{

    private volatile boolean running = true;
    ChessEngine engine;

    private Queue<EngineCommand> commands = new ConcurrentLinkedQueue<>();

   public ChessEngineRunner()
   {
       this.engine = new ChessEngine();
   }

   public void post(EngineCommand command)
   {
       this.commands.offer(command);
   }

    @Override
    public void run() {

       while(running)
       {
           if(!commands.isEmpty())
           {
               EngineCommand command = commands.poll();

               if(command instanceof StartNewGame)
               {
                   engine.setTheNewGame(((StartNewGame) command).getPlayerOne(),
                           ((StartNewGame) command).getPlayerTwo());

               }


           }
       }
    }
}
