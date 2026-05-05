package org.portfolio.benttchessgame.chessengine;


import org.portfolio.benttchessgame.chessengine.commands.EngineCommand;
import org.portfolio.benttchessgame.chessengine.commands.MakeMove;
import org.portfolio.benttchessgame.chessengine.commands.ProposeMove;
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

   public ChessEngine getEngine()
   {
       return this.engine;
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

               if(command instanceof StartNewGame ng)
               {
                   engine.setTheNewGame(ng.getPlayerOne(), ng.getPlayerTwo());

               }
               else if(command instanceof ProposeMove p)
               {
                   engine.proposeMove(p.getRow(), p.getCol());
               }
               else if(command instanceof MakeMove m)
               {
                   engine.moveTheFigure(m.getMoveOperation());
               }


           }
       }
    }
}
