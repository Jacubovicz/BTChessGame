package UILogic.helperclasses;

import javafx.scene.layout.StackPane;
import org.portfolio.benttchessgame.chessengine.Cell;

import java.util.ArrayList;
import java.util.Map;

public class Painter {

    private final StackPane[][] gameBoard; //grid

    private ArrayList<Cell> avlMoves;// cells marked as green


    Map<Styles, String> stylesMap; // styles enum

    public enum Styles
    {
        AVL_STYLE, ENEMY_STYLE, CLICKED_MOVED_STYLE, NO_STYLE;
    }


   public Painter(StackPane[][] gameBoard)
   {
       this.gameBoard = gameBoard;
       avlMoves = new ArrayList<>();

       stylesMap = Map.of(Styles.AVL_STYLE, "-fx-border-color: green; -fx-border-width:5px;",
               Styles.ENEMY_STYLE, "-fx-border-color: red; -fx-border-width:5px;",
               Styles.CLICKED_MOVED_STYLE, "-fx-border-color: yellow; -fx-border-width: 5px;",
               Styles.NO_STYLE, "");
   }

   public void paint(Styles s, int row, int col)
   {
       gameBoard[row][col].setStyle(stylesMap.get(s));
   }

   public void paint(Styles s, ArrayList<Cell> avlMoves)
   {
       this.avlMoves = avlMoves;

       for(Cell c : avlMoves)
       {
           gameBoard[c.getRow()][c.getCol()].setStyle(stylesMap.get(s));
       }
   }

   public void removePaint()
   {
           paint(Styles.NO_STYLE, avlMoves);

           avlMoves.clear();
   }

   public boolean isInAvailableMoves(int row, int col)
   {
       for(Cell singleMove : avlMoves)
       {
           if(row == singleMove.getRow() && col == singleMove.getCol()) return true;
       }
       return false;
   }

}
