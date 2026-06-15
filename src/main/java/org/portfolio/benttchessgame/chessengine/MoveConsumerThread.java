package org.portfolio.benttchessgame.chessengine;

public class MoveConsumerThread extends Thread{

    Game currentGame;
    Session currentSession;

    public MoveConsumerThread(Session session)
    {
        this.currentSession = session;
        this.currentGame = session.getCurrentGame();

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
                  currentGame.move(currentSession.getOperationQueue().poll(), false);
              }
              else
              {
                  currentGame.move(currentSession.getOperationQueue().poll(), true);
              }
          }
      }
    }
}
