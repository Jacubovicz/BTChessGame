package org.portfolio.benttchessgame.chessengine.players;

//This is the player for offline pvp purposes.

import org.portfolio.benttchessgame.chessengine.movement.PieceBoardManager;
import org.portfolio.benttchessgame.chessengine.Session;

import java.util.logging.Logger;

public class BasePlayer extends Player{

    static Logger log = Logger.getLogger(BasePlayer.class.getName());

    //it's going to be fetched randomly and colours will be assigned as names.

    public BasePlayer(String name)
    {
        super(name);
    }


}
