package UILogic.helperclasses;

import javafx.scene.layout.StackPane;
import org.portfolio.benttchessgame.chessengine.Cell;

import java.util.ArrayList;
import java.util.Map;

public class Painter {

    private final StackPane[][] gameBoard; //grid
    private ArrayList<Cell> activeClickableFields  = new ArrayList<>();

    private final ArrayList<Cell> clickedMovedBuffer = new ArrayList<>();
    private final ArrayList<Cell> avlMovesColouredBuffer = new ArrayList<>();
    private final ArrayList<Cell> castlingCells = new ArrayList<>();

    Map<Styles, String> stylesMap; // styles enum

    public enum Styles
    {
        AVL_STYLE, ENEMY_STYLE, CLICKED_MOVED_STYLE, CASTLING_STYLE, NO_STYLE, CHECKMATE_STYLE;
    }

   public Painter(StackPane[][] gameBoard)
   {
       this.gameBoard = gameBoard;

       stylesMap = Map.of(Styles.AVL_STYLE, "-fx-border-color: green; -fx-border-width:5px;",
               Styles.ENEMY_STYLE, "-fx-border-color: red; -fx-border-width:5px;",
               Styles.CLICKED_MOVED_STYLE, "-fx-border-color: yellow; -fx-border-width: 5px;",
               Styles.CASTLING_STYLE, "-fx-border-color: blue; -fx-border-width: 5px",
               Styles.CHECKMATE_STYLE, "-fx-border-color: darkred; -fx-border-width: 5px",
               Styles.NO_STYLE, "");
   }

   private void paint(Styles s, Cell cell)
   {
       if(s == Styles.CLICKED_MOVED_STYLE)
       {
           gameBoard[cell.getRow()][cell.getCol()].setStyle(stylesMap.get(s));
           clickedMovedBuffer.add(cell);
       }

   }

   private void paint(Styles s, ArrayList<Cell> cellsToPaint)
   {
       if(cellsToPaint.isEmpty()) return;

       ArrayList<Cell> temp = new ArrayList<>();

       for(Cell c : cellsToPaint)
       {
           if(c != null)
           {
               if(!clickedMovedBuffer.contains(c))
               {
                   if(c.isCastlingCell())
                   {
                       gameBoard[c.getRow()][c.getCol()].setStyle(stylesMap.get(Styles.CASTLING_STYLE));
                       castlingCells.add(c);
                       continue;
                   }
                   gameBoard[c.getRow()][c.getCol()].setStyle(stylesMap.get(s));
               }
           }
           temp.add(c);
       }

       if(s == Styles.AVL_STYLE)
       {
           avlMovesColouredBuffer.addAll(temp);
       }
   }

   // the name of a method needs to be changed for "handleAvlMoves"
   public void paintAvlMoves(ArrayList<Cell> avlMoves)
   {
       removePaintAndDisableDelete(avlMovesColouredBuffer);
       removePaintAndDisableDelete(castlingCells);

       paint(Styles.AVL_STYLE, avlMoves);
       activateAvlFields(avlMovesColouredBuffer);
       activateAvlFields(castlingCells);
   }

   public void removePaintAndDisableDelete(ArrayList<Cell> cellsForRemoval)
   {
       for(Cell cell : cellsForRemoval)
       {
           if(cell != null && !clickedMovedBuffer.contains(cell))
           {
               int row = cell.getRow();
               int col = cell.getCol();

               gameBoard[row][col].setStyle(stylesMap.get(Styles.NO_STYLE));
               gameBoard[row][col].setDisable(true);
           }
           else if(cell != null && clickedMovedBuffer.contains(cell))
           {
                gameBoard[cell.getRow()][cell.getCol()].setDisable(true);
           }
       }
       cellsForRemoval.clear();
   }

   public void activateAvlFields(ArrayList<Cell> fields)
   {
       if(fields.isEmpty()) return;

       for(Cell field : fields)
       {
           if(field != null)
           {
               gameBoard[field.getRow()][field.getCol()].setDisable(false);
           }
       }
   }

   public void setClickableFields(ArrayList<Cell> clickableFields)
   {
       disableFieldsOf(activeClickableFields);
       ArrayList<Cell> tempCells = new ArrayList<>();

       /// test only
       for(int i = 0; i< gameBoard.length; i++)
       {
           for(int j = 0; j <gameBoard.length; j++)
           {
               gameBoard[i][j].setStyle(stylesMap.get(Styles.NO_STYLE));
           }
       }
       /// end test

      for(Cell cell : clickableFields)
      {
          if(cell != null)
          {
              gameBoard[cell.getRow()][cell.getCol()].setDisable(false);
              tempCells.add(new Cell(cell.getRow(), cell.getCol()));

              ///  test only

              gameBoard[cell.getRow()][cell.getCol()].setStyle("-fx-border-color: pink; -fx-border-width:5px");
              /// end test
          }
      }
      activeClickableFields = new ArrayList<>(tempCells);
   }

   private void disableFieldsOf(ArrayList<Cell> clickableFields)
   {
       if(clickableFields.isEmpty()) return;

       for(Cell c : clickableFields)
       {
           if(c != null)
           {
               gameBoard[c.getRow()][c.getCol()].setDisable(true);
           }
       }

       clickableFields.clear();
   }

   public void paintClickedOrMoved(Cell cell) {

       if(clickedMovedBuffer.size() == 2)
       {
           cleanTheClickedMovedBufferAndRepaint();
       }
       if(isInAvailableMoves(cell.getRow(), cell.getCol()))
       {
           paint(Styles.CLICKED_MOVED_STYLE, cell);
       }
       else if(clickedMovedBuffer.size() == 1)
       {
           cleanTheClickedMovedBufferAndRepaint();
           paint(Styles.CLICKED_MOVED_STYLE, cell);
       }
       else
           paint(Styles.CLICKED_MOVED_STYLE, cell);
   }

   public void cleanTheClickedMovedBufferAndRepaint()
   {
       for(Cell c : clickedMovedBuffer)
       {
           if(c != null) gameBoard[c.getRow()][c.getCol()].setStyle(stylesMap.get(Styles.NO_STYLE));
       }
       clickedMovedBuffer.clear();
   }

   public boolean isInAvailableMoves(int row, int col)
   {
       Cell temp = new Cell(row, col);

       if(avlMovesColouredBuffer.isEmpty()) return false;

       //find out what is the deal with castling cell.
       return avlMovesColouredBuffer.contains(temp) || castlingCells.contains(temp);

   }

}
