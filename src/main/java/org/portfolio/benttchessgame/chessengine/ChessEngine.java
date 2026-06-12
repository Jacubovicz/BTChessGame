package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.players.BasePlayer;
import org.portfolio.benttchessgame.chessengine.players.Player;
import org.portfolio.benttchessgame.figures.King;


public class ChessEngine{

    Game currentGame;
    Player player1, player2;
    Validator validator;
    MoveEvaluator simulator;


    public ChessEngine()
    {
        Player player1= new BasePlayer("Kuba");
        Player player2 = new BasePlayer("Kinga");

        currentGame = new Game(player1, player2);
        this.validator = new Validator(currentGame);
        this.simulator = new MoveEvaluator(validator);
    }

    public Validator getValidator()
    {
        return this.validator;
    }

    public Game getCurrentGame()
    {
        return this.currentGame;
    }

    public void setTheNewGame(Player player1, Player player2)
    {
        this.player1 = player1;
        this.player2 = player2;
        currentGame = new Game(player1, player2);

        System.out.print("Succesfully initialized new game for:\n " + player1.getName() + "\n" + player2.getName());
    }

    public void proposeMove(int row, int col)
    {
        Cell cell = new Cell(row, col);
        validator.checkMoves(cell);
    }

    public void moveTheFigure(MoveOperation operation)
    {
        if(currentGame.getSnapshot()[operation.getInitCell().getRow()][operation.getInitCell().getCol()] instanceof King k)
        {
            if(k.getCastling() != null)
            {
                currentGame.move(k.getCastling());
                k.setCastling(null);
            }
        }
        currentGame.move(operation);

    }

    public void resetTheGame()
    {
        currentGame = new Game(player1, player2);
    }
}
