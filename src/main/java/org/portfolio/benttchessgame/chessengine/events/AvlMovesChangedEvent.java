package org.portfolio.benttchessgame.chessengine.events;

import UILogic.helperclasses.EnumColors;
import org.portfolio.benttchessgame.chessengine.Cell;

import java.util.ArrayList;
import java.util.HashMap;

public record AvlMovesChangedEvent(ArrayList<Cell> avlMoves) { }
