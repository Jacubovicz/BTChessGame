package UILogic.helperclasses;

import javafx.scene.layout.StackPane;
import org.portfolio.benttchessgame.chessengine.Cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Painter {



    private final StackPane[][] gameBoard; //grid
    private final ArrayList<StackPane> cleanupBuffer = new ArrayList<>();

    private HashMap<EnumColors, ArrayList<Cell>> allMoves;

    private ArrayList<Cell> avlMovesCells;
    private ArrayList<Cell> clickedMovedCells;
    private ArrayList<Cell> checkmateCells;
    private ArrayList<Cell> castlingCell;
    private ArrayList<Cell> clickableFields;

    Map<Styles, String> stylesMap; // styles enum

    public enum Styles
    {
        AVL_STYLE, ENEMY_STYLE, CLICKED_MOVED_STYLE, CASTLING_STYLE, NO_STYLE;
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

       allMoves = new HashMap<>();
       avlMovesCells = new ArrayList<>();
       checkmateCells = new ArrayList<>();
       clickedMovedCells = new ArrayList<>();
       clickableFields = new ArrayList<>();


       stylesMap = Map.of(Styles.AVL_STYLE, "-fx-border-color: green; -fx-border-width:5px;",
               Styles.ENEMY_STYLE, "-fx-border-color: red; -fx-border-width:5px;",
               Styles.CLICKED_MOVED_STYLE, "-fx-border-color: yellow; -fx-border-width: 5px;",
               Styles.CASTLING_STYLE, "-fx-border-color: blue; -fx-border-width: 5px",
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

   private void paint(Styles s, ArrayList<Cell> cellsToPaint)
   {
       for(Cell c : cellsToPaint)
       {
           if(c != null)
           {
               gameBoard[c.getRow()][c.getCol()].setStyle(stylesMap.get(s));
           }

       }
   }

   public void paintAvlMoves(HashMap<EnumColors, ArrayList<Cell>> allMoves)
   {
       this.allMoves = allMoves;

       for(EnumColors color : allMoves.keySet())
       {
           ArrayList<Cell> listToPaint = allMoves.get(color);
           if(color == EnumColors.BLANK)
           {
               setClickableFields(clickableFields, true);
               this.clickableFields = listToPaint;
               setClickableFields(clickableFields, false);
           }
           if(color == EnumColors.GREEN)
           {
               setClickableFields(avlMovesCells, true);
               this.avlMovesCells = listToPaint;
               setClickableFields(avlMovesCells, false);
               paint(Styles.AVL_STYLE, avlMovesCells);
           }
           if(color == EnumColors.RED)
           {
               this.checkmateCells = listToPaint;
               paint(Styles.ENEMY_STYLE, checkmateCells);
           }
           if(color == EnumColors.YELLOW)
           {
               this.clickedMovedCells = listToPaint;
               paint(Styles.CLICKED_MOVED_STYLE, clickedMovedCells);
           }
           if(color == EnumColors.BLUE)
           {
               if(castlingCell != null) setClickableFields(castlingCell, true);
               this.castlingCell = listToPaint;
               setClickableFields(castlingCell, false);
               paint(Styles.CASTLING_STYLE, castlingCell);
           }
       }
   }

   private void setClickableFields(ArrayList<Cell> clickableFields, boolean disable)
   {
      for(Cell cell : clickableFields)
      {
          if(cell != null)
          {
              gameBoard[cell.getRow()][cell.getCol()].setDisable(disable);
          }
      }
   }

   public void paintClickedOrMoved(int row, int col)
   {
       paint(Styles.CLICKED_MOVED_STYLE, row, col);
   }

   public void removeAvlMvsPaint() //removes only the avlMoves paint
   {
       for(EnumColors c : allMoves.keySet())
       {
           if(c == EnumColors.GREEN /*|| c == EnumColors.YELLOW*/)
           {
               paint(Styles.NO_STYLE, allMoves.get(c));
           }
       }
           allMoves.clear();
   }


   public boolean isInAvailableMoves(int row, int col)
   {
       Cell temp = new Cell(row, col);

       return avlMovesCells.contains(temp) || (castlingCell == null ? false : castlingCell.contains(temp));

   }

}
