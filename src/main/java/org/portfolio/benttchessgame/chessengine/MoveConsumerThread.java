package org.portfolio.benttchessgame.chessengine;

import java.util.Objects;

public class MoveConsumerThread extends Thread{

    PieceBoardManager pieceBoardManager;
    Session currentSession;

    public MoveConsumerThread(Session session, PieceBoardManager pieceBoardManager)
    {
        this.currentSession = session;
        this.pieceBoardManager = pieceBoardManager;
        this.start();
    }

    public void run() //this code needs to be optimized
    {
      while(currentSession.isInGame())
      {
          while(currentSession.getOperationQueue() != null && !currentSession.getOperationQueue().isEmpty())
          {
              int size = currentSession.getOperationQueue().size();
              if(size > 1)
              {
                  pieceBoardManager.move(Objects.requireNonNull(currentSession.getOperationQueue().poll()), currentSession,true);
              }
              else
              {
                  pieceBoardManager.move(Objects.requireNonNull(currentSession.getOperationQueue().poll()), currentSession, true);
                  currentSession.switchTurn();
                  pieceBoardManager.fillTheStarterMoves(currentSession.getTurn());
              }
          }
      }
    }
}
