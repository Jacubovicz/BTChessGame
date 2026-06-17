package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.players.BasePlayer;
import org.portfolio.benttchessgame.chessengine.players.Player;
import org.portfolio.benttchessgame.figures.King;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ChessEngine{

    private Game currentGame;
    private Player player1, player2;
    private Validator validator;
    private MoveEvaluator simulator;
    private Session gameSession;

    private boolean turn;


    public ChessEngine()
    {
        this.player1= new BasePlayer("Kuba");
        this.player2 = new BasePlayer("Kinga");

        this.gameSession = new Session(player1, player2);

        currentGame = new Game(gameSession);
        gameSession.setCurrentGame(currentGame);
        this.validator = new Validator(currentGame);
        this.simulator = new MoveEvaluator(validator);

        this.turn = player1.getColor() ? player1.getColor() : player2.getColor();
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
        this.gameSession = new Session(player1, player2);

        currentGame = new Game(gameSession);

        System.out.print("Succesfully initialized new game for:\n " + player1.getName() + "\n" + player2.getName());
    }

    public void proposeMove(int row, int col)
    {
        Cell cell = new Cell(row, col);
        validator.checkMoves(cell);
    }

    public void moveTheFigure(MoveOperation operation)
    {
        Queue<MoveOperation> moveOperationList = new ConcurrentLinkedQueue<>();
        if(currentGame.getSnapshot()[operation.getInitCell().getRow()][operation.getInitCell().getCol()] instanceof King k)
        {
            if(k.getCastling() != null)
            {
                moveOperationList.add(k.getCastling());
                k.setCastling(null);
            }
        }

        moveOperationList.add(operation);
        boolean currentTurn = currentGame.currentSession.getTurn();

        this.gameSession.publishMoves(moveOperationList);

    }

    public void resetTheGame()
    {
        currentGame = new Game(new Session(player1, player2));
    }
}
