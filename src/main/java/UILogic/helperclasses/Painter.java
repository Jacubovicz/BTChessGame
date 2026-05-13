package UILogic.helperclasses;

import javafx.scene.layout.StackPane;
import org.portfolio.benttchessgame.chessengine.Cell;

import java.util.ArrayList;
import java.util.Map;

public class Painter {

    private final StackPane[][] gameBoard; //grid
    private final ArrayList<StackPane> cleanupBuffer = new ArrayList<>();

    private ArrayList<Cell> avlMoves;// cells marked as green


    Map<Styles, String> stylesMap; // styles enum

    public enum Styles
    {
        AVL_STYLE, ENEMY_STYLE, CLICKED_MOVED_STYLE, NO_STYLE;
    }

    public void addToCleanupBuffer(Cell cell) //This is adding to the list as yellow clicks
    {
        StackPane stP1 = gameBoard[cell.getRow()][cell.getCol()];
        paint(Styles.CLICKED_MOVED_STYLE, stP1);
        cleanupBuffer.add(stP1); //This needs to be cleaned with new iteration

    }

    public void cleanTheBuffer() // removing the coulours from the board.
    {
        for(StackPane s : cleanupBuffer)
        {
            s.setStyle(stylesMap.get(Styles.NO_STYLE));
        }
        cleanupBuffer.clear();
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

   private void paint(Styles s, StackPane stackPane)
   {
       stackPane.setStyle(stylesMap.get(s));
   }

   private void paint(Styles s, int row, int col)
   {
       gameBoard[row][col].setStyle(stylesMap.get(s));
   }

   private void paint(Styles s, ArrayList<Cell> avlMoves)
   {
       this.avlMoves = avlMoves;

       for(Cell c : avlMoves)
       {
           if(c != null)
           {
               gameBoard[c.getRow()][c.getCol()].setStyle(stylesMap.get(s));
           }

       }
   }

   public void paintAvlMoves(ArrayList<Cell> avlMoves)
   {
       paint(Styles.AVL_STYLE, avlMoves);
   }

   public void paintClickedOrMoved(int row, int col)
   {
       paint(Styles.CLICKED_MOVED_STYLE, row, col);
   }

   public void removeAvlMvsPaint() //removes only the avlMoves paint
   {
           paint(Styles.NO_STYLE, avlMoves);

           avlMoves.clear();
   }

   public void removeAllPaintIncl(int row, int col) // paints avlMoves and the clicked field.
   {
       removeAvlMvsPaint();
       paint(Styles.NO_STYLE, row,col);

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
