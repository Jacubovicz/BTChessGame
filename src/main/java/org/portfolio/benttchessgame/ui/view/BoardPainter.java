package org.portfolio.benttchessgame.ui.view;

import javafx.scene.layout.StackPane;
import org.portfolio.benttchessgame.engine.BoardSquare;

import java.util.ArrayList;
import java.util.Map;

public class BoardPainter {

    private final StackPane[][] gameBoard; //grid
    private ArrayList<BoardSquare> activeClickableFields  = new ArrayList<>();

    private final ArrayList<BoardSquare> clickedMovedBuffer = new ArrayList<>();
    private final ArrayList<BoardSquare> avlMovesColouredBuffer = new ArrayList<>();
    private final ArrayList<BoardSquare> castlingBoardSquares = new ArrayList<>();

    Map<Styles, String> stylesMap; // styles enum

    public enum Styles
    {
        AVL_STYLE, ENEMY_STYLE, CLICKED_MOVED_STYLE, CASTLING_STYLE, NO_STYLE, CHECKMATE_STYLE;
    }

   public BoardPainter(StackPane[][] gameBoard)
   {
       this.gameBoard = gameBoard;

       stylesMap = Map.of(Styles.AVL_STYLE, "-fx-border-color: green; -fx-border-width:5px;",
               Styles.ENEMY_STYLE, "-fx-border-color: red; -fx-border-width:5px;",
               Styles.CLICKED_MOVED_STYLE, "-fx-border-color: yellow; -fx-border-width: 5px;",
               Styles.CASTLING_STYLE, "-fx-border-color: blue; -fx-border-width: 5px",
               Styles.CHECKMATE_STYLE, "-fx-border-color: darkred; -fx-border-width: 5px",
               Styles.NO_STYLE, "");
   }

   private void paint(Styles s, BoardSquare boardSquare)
   {
       if(s == Styles.CLICKED_MOVED_STYLE)
       {
           gameBoard[boardSquare.getRow()][boardSquare.getCol()].setStyle(stylesMap.get(s));
           clickedMovedBuffer.add(boardSquare);
       }

   }

   private void paint(Styles s, ArrayList<BoardSquare> cellsToPaint)
   {
       if(cellsToPaint.isEmpty()) return;

       ArrayList<BoardSquare> temp = new ArrayList<>();

       for(BoardSquare c : cellsToPaint)
       {
           if(c != null)
           {
               if(!clickedMovedBuffer.contains(c))
               {
                   if(c.isCastlingCell())
                   {
                       gameBoard[c.getRow()][c.getCol()].setStyle(stylesMap.get(Styles.CASTLING_STYLE));
                       castlingBoardSquares.add(c);
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
   public void paintAvlMoves(ArrayList<BoardSquare> avlMoves)
   {
       removePaintAndDisableDelete(avlMovesColouredBuffer);
       removePaintAndDisableDelete(castlingBoardSquares);

       paint(Styles.AVL_STYLE, avlMoves);
       activateAvlFields(avlMovesColouredBuffer);
       activateAvlFields(castlingBoardSquares);
   }

   public void removePaintAndDisableDelete(ArrayList<BoardSquare> cellsForRemoval)
   {
       for(BoardSquare boardSquare : cellsForRemoval)
       {
           if(boardSquare != null && !clickedMovedBuffer.contains(boardSquare))
           {
               int row = boardSquare.getRow();
               int col = boardSquare.getCol();

               gameBoard[row][col].setStyle(stylesMap.get(Styles.NO_STYLE));
               gameBoard[row][col].setDisable(true);
           }
           else if(boardSquare != null && clickedMovedBuffer.contains(boardSquare))
           {
                gameBoard[boardSquare.getRow()][boardSquare.getCol()].setDisable(true);
           }
       }
       cellsForRemoval.clear();
   }

   public void activateAvlFields(ArrayList<BoardSquare> fields)
   {
       if(fields.isEmpty()) return;

       for(BoardSquare field : fields)
       {
           if(field != null)
           {
               gameBoard[field.getRow()][field.getCol()].setDisable(false);
           }
       }
   }

   public void setClickableFields(ArrayList<BoardSquare> clickableFields)
   {
       disableFieldsOf(activeClickableFields);
       ArrayList<BoardSquare> tempBoardSquares = new ArrayList<>();

       /// test only
       for(int i = 0; i< gameBoard.length; i++)
       {
           for(int j = 0; j <gameBoard.length; j++)
           {
               gameBoard[i][j].setStyle(stylesMap.get(Styles.NO_STYLE));
           }
       }
       /// end test

      for(BoardSquare boardSquare : clickableFields)
      {
          if(boardSquare != null)
          {
              gameBoard[boardSquare.getRow()][boardSquare.getCol()].setDisable(false);
              tempBoardSquares.add(new BoardSquare(boardSquare.getRow(), boardSquare.getCol()));

              ///  test only

              gameBoard[boardSquare.getRow()][boardSquare.getCol()].setStyle("-fx-border-color: pink; -fx-border-width:5px");
              /// end test
          }
      }
      activeClickableFields = new ArrayList<>(tempBoardSquares);
   }

   private void disableFieldsOf(ArrayList<BoardSquare> clickableFields)
   {
       if(clickableFields.isEmpty()) return;

       for(BoardSquare c : clickableFields)
       {
           if(c != null)
           {
               gameBoard[c.getRow()][c.getCol()].setDisable(true);
           }
       }

       clickableFields.clear();
   }

   public void paintClickedOrMoved(BoardSquare boardSquare) {

       if(clickedMovedBuffer.size() == 2)
       {
           cleanTheClickedMovedBufferAndRepaint();
       }
       if(isInAvailableMoves(boardSquare.getRow(), boardSquare.getCol()))
       {
           paint(Styles.CLICKED_MOVED_STYLE, boardSquare);
       }
       else if(clickedMovedBuffer.size() == 1)
       {
           cleanTheClickedMovedBufferAndRepaint();
           paint(Styles.CLICKED_MOVED_STYLE, boardSquare);
       }
       else
           paint(Styles.CLICKED_MOVED_STYLE, boardSquare);
   }

   public void cleanTheClickedMovedBufferAndRepaint()
   {
       for(BoardSquare c : clickedMovedBuffer)
       {
           if(c != null) gameBoard[c.getRow()][c.getCol()].setStyle(stylesMap.get(Styles.NO_STYLE));
       }
       clickedMovedBuffer.clear();
   }

   public boolean isInAvailableMoves(int row, int col)
   {
       BoardSquare temp = new BoardSquare(row, col);

       if(avlMovesColouredBuffer.isEmpty()) return false;

       //find out what is the deal with castling cell.
       return avlMovesColouredBuffer.contains(temp) || castlingBoardSquares.contains(temp);

   }

}
