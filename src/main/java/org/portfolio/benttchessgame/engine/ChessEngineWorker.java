package org.portfolio.benttchessgame.engine;


import org.portfolio.benttchessgame.engine.command.GameCommand;
import org.portfolio.benttchessgame.engine.command.MovePieceCommand;
import org.portfolio.benttchessgame.engine.command.RequestLegalMovesCommand;
import org.portfolio.benttchessgame.engine.command.StartGameCommand;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChessEngineWorker implements Runnable {

    private volatile boolean running = true;
    ChessEngine engine;

    private Queue<GameCommand> commands = new ConcurrentLinkedQueue<>();

    public ChessEngineWorker() {
        // this.engine = new ChessEngine(new OfflineBridgeImpl());

    }

    public void setEngine(ChessEngine engine)
    {
        this.engine = engine;
    }

   public ChessEngine getEngine()
   {
       return this.engine;
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

               if(command instanceof StartGameCommand st)
               {
                   engine.setTheNewGame(st.getPlayerOne(), st.getPlayerTwo(), st.isOfflineGame());
               }
               else if(command instanceof RequestLegalMovesCommand p)
               {
                   engine.proposeMove(p.getRow(), p.getCol());
               }
               else if(command instanceof MovePieceCommand m)
               {
                   try {
                       engine.moveTheFigure(m.getMoveOperation());
                   } catch (IllegalMoveException e) {
                       throw new RuntimeException(e);
                   }
               }
           }
       }
    }
}
