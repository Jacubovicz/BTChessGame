package org.portfolio.benttchessgame.chessengine.listeners;

import UILogic.helperclasses.EnumColors;
import org.portfolio.benttchessgame.chessengine.Cell;

import java.util.ArrayList;
import java.util.HashMap;

@FunctionalInterface
public interface AvailableMovesChangeListener {

    public void onMovesChangeEvent(HashMap<EnumColors, ArrayList<Cell>> moves);
}
