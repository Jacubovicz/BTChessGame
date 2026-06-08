package org.portfolio.benttchessgame.chessengine.players;

import org.portfolio.benttchessgame.chessengine.MoveOperation;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Player {



   private final String name;
   private boolean color;

   private Queue<MoveOperation> movements;

   public final boolean WHITE = true;
   public final boolean BLACK = false;



  /* protected*/ Player(String name)
   {
       movements = new ConcurrentLinkedQueue<>();
       this.name = name;
   }

   public boolean getColor()
   {
       return this.color;
   }

   public void setColor(boolean color)
   {
       this.color = color;
   }


   public String getName()
   {
       return this.name;
   }

   public Queue<MoveOperation> getMovements()
   {
       return movements;
   }

   public MoveOperation performMove()
   {
       boolean movementPerformed = false;
       MoveOperation operation = null;

      while(movementPerformed == false)
      {
          if(!movements.isEmpty())
          {
              movementPerformed = true;
              operation = this.movements.poll();
          }

      }

    return operation;
   }

   //The more suitable name would be publishMove
   public abstract void publishMove(MoveOperation operation);
}
