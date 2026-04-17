package org.portfolio.benttchessgame;

import UILogic.UIEngine;
import javafx.application.Application;
import javafx.stage.Stage;
import org.portfolio.benttchessgame.chessengine.ChessEngine;
import org.portfolio.benttchessgame.chessengine.players.BasePlayer;


import java.io.IOException;

public class BenTChessGameApplication extends Application {



    @Override
    public void start(Stage stage) throws IOException {



        UIEngine ui = UIEngine.init(stage);


    }

    public static void main(String[] args) {


       launch();

      //  Board board = new Board();
       // board.printBoard();

      //  Game game = new Game(new AIPlayer("P1"), new AIPlayer("P2"));
       // game.getSnapshot().printBoard();
      //  System.out.println();
       // System.out.print("White player: " + game.getTheWhitePlayer().getName() + " \n");
       // System.out.print("Dark player: " + game.getTheDarkPlayer().getName());
       // System.out.print("\n\n");
       // game.move(false,);
       // System.out.print("\n\n");
       // game.getSnapshot().printBoard();

     //   ONLY_TEST_RUNNER runner = new ONLY_TEST_RUNNER(new BasePlayer("p1"), new BasePlayer("p2"));
      //  Thread t = new Thread(runner);
     //   t.start();


    }
}