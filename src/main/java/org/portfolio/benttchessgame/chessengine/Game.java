package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.listeners.BoardChangedListener;
import org.portfolio.benttchessgame.chessengine.listeners.StarterMovesAvailableListener;
import org.portfolio.benttchessgame.chessengine.listeners.TimeChangeListener;
import org.portfolio.benttchessgame.chessengine.listeners.TurnChangedListener;
import org.portfolio.benttchessgame.chessengine.players.Player;
import org.portfolio.benttchessgame.figures.Figure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/*
* This is the class(object) representing the whole logic for the Game.
* it represents the current session together with PLAYERS taking part in it
* but also Board, the snapshot version of it from JavaFX will take data
* Scoring system will be implemented here as well as it is logically part of this specific Game data
* and counting system(time)
 */
public class Game {

    //The Board object.
    Board currentBoard;

    //the snapshot with positions of pieces on the board
    Figure[][] snapshot;

    Cell[] positions = new Cell[2];


    //References to players
    Player p1, p2;
    //indicator of turns.
    boolean turn;

    //randomly chosen color of piece
    Random random = new Random(); // this is only for tests

    List<BoardChangedListener> listeners = new ArrayList<>();

    String time = "0";
    List<TimeChangeListener> timeListeners = new ArrayList<>();

    List<TurnChangedListener> turnListeners = new ArrayList<>();

    List<StarterMovesAvailableListener> starterListeners = new ArrayList<>();
    /*
    * Game object is initialized with Player1 and Player2 parameters, essentials needed for creation
    * Currently it has randomizer of colors however this is not the best solution and will be
    * overloaded in the other constructor
    * it also creates the initial version of snapshot which is 1:1 representation of original
     */


    public Game(Player player1, Player player2)
    {
        this.p1 = player1;
        this.p2 = player2;

        this.turn = random.nextBoolean();
        player1.setColor(turn);
        player2.setColor(!turn);

        this.currentBoard = new Board(player1.getColor(), player2.getColor());
        snapshot = currentBoard.getSnapshot();

        //counts the time (temporarily for tests - needs refactoring)
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int i = 0; i < 10000; i++)
                    {
                        Thread.sleep(1000);
                        time = String.valueOf(i);
                        System.out.print(time + " ");
                        notifyTimerListeners();
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();

        setTurn(true);
    }

    public void setTurn(boolean turn)
    {
        this.turn = turn;

        notifyTurnListeners();
    }

    public void setPosition(int position, Cell cell)
    {
        positions[position] = cell;
        if(position == 1 && positions[0] != null)
        {
            MoveOperation op = new MoveOperation(positions[0], positions[1]);
            move(op);
        }
    }

    /*
    * returning the snapshot.
     */
    public Figure[][] getSnapshot()
    {
        return currentBoard.getSnapshot();
    }

    public boolean[][] getStarterMoves()
    {
        return currentBoard.getStarterMovesAvailable();
    }

    public void setStarterMoves(int row, int col, boolean val)
    {
        this.currentBoard.getStarterMovesAvailable()[row][col] = val;
        notifyStarterMovesListeners();
    }

    /*
    * returning the Player object of White figures
     */
    public Player getTheWhitePlayer()
    {
        return p1.getColor() == p1.WHITE ? p1 : p2;
    }

    /*
    * returning the Player object of Dark figures
     */
    public Player getTheDarkPlayer()
    {
        return p2.getColor() == p2.BLACK ? p2 : p1;
    }

    /* NOW THE MAGIC: Movement of objects. This is the version demo. Tests only.
     * X AND Y are the indexes of the Board objects
     * TOP LEFT CORNER is X = 0. Y = 0
     * BOTTOM RIGHT CORNER is X = 7, Y = 7
     * X GOING DOWN FROM 0 to 7
     * Y GOING RIGHT FROM 0 TO 7
     * tokenColor is the actual color of figure objects you're playing with.
     * WHITE = true, DARK = false
     */
    public void move(MoveOperation moveOperation)
    {
        Cell from = moveOperation.getInitCell();
        Cell to = moveOperation.getDestCell();

        Figure[][] board = currentBoard.getBoard();


        //As of now, this only performs hardcoded movement and overrides whatever there is in position.
        if(board[from.getRow()][from.getCol()] != null)
        {
                Figure f= currentBoard.getBoard()[from.getRow()][from.getCol()];
                currentBoard.getBoard()[from.getRow()][from.getCol()] = null;
                currentBoard.getBoard()[to.getRow()][to.getCol()] = f;

              this.turn = !this.turn;

              currentBoard.fillTheStarterMoves(turn);
            notifyListeners();
            notifyTurnListeners();
            notifyStarterMovesListeners();
            }
    }

    public void notifyListeners()
    {
        for(BoardChangedListener listener : listeners)
        {
            listener.onBoardChanged(currentBoard.getSnapshot());
        }
    }

    public void notifyTimerListeners()
    {
        for(TimeChangeListener listener : timeListeners)
        {
            listener.onTimeChange(time);
        }
    }

    public void addBoardChangedListener(BoardChangedListener listener)
    {
        listeners.add(listener);
    }

    public void removeBoardChangedListener(BoardChangedListener listener)
    {
        listeners.remove(listener);
    }

    public void addTimeChangeListener(TimeChangeListener listener)
    {
        timeListeners.add(listener);
    }

    public void removeTimeChangeListener(TimeChangeListener listener)
    {
        timeListeners.remove(listener);
    }

    public void addTurnListener(TurnChangedListener listener)
    {
        turnListeners.add(listener);
    }

    public void removeTurnListener(TurnChangedListener listener)
    {
        turnListeners.remove(listener);
    }

    public void notifyTurnListeners()
    {
        for(TurnChangedListener listener : turnListeners)
        {
            listener.onTurnChanged(turn);
        }
    }

    public void addStarterMovesAvailableListener(StarterMovesAvailableListener listener)
    {
        starterListeners.add(listener);
    }

    public void removeStarterMovesAvailableListener(StarterMovesAvailableListener listener)
    {
        starterListeners.remove(listener);
    }

    public void notifyStarterMovesListeners()
    {
        for(StarterMovesAvailableListener listener : starterListeners)
        {
            listener.onStarterMovesAvailableChange(currentBoard.getStarterMovesAvailable());
        }
    }


}
    //How about move the coordinates fromX, fromY, toX, toY, be received from the Player object?
    // then the interface Player would have sense, and Gamerunner would wait for the specific command
    //performMove, while the Game object track the turn basis.

    //this is for test also.


        //listeners
