package org.portfolio.benttchessgame.chessengine.players;

import org.portfolio.benttchessgame.chessengine.AIEngine;
import org.portfolio.benttchessgame.chessengine.movement.PieceBoardManager;
import org.portfolio.benttchessgame.chessengine.Session;

public class AIPlayer extends Player{

    public AIPlayer()
    {
        super("AIPlayer");
    }

    public AIPlayer(String name)
    {
        super("AIPlayer: " + name);
    }

}
