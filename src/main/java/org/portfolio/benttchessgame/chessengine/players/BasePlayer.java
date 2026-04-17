package org.portfolio.benttchessgame.chessengine.players;

//This is the player for offline pvp purposes.

import org.portfolio.benttchessgame.chessengine.MoveOperation;

import java.util.logging.Logger;

public class BasePlayer extends Player{

    static Logger log = Logger.getLogger(BasePlayer.class.getName());

    //it's going to be fetched randomly and colours will be assigned as names.

    public BasePlayer(String name)
    {
        super("BasePlayer " + name);
    }

    @Override
    public void publishMove(MoveOperation operation) {
        super.getMovements().offer(operation);
    }


}
