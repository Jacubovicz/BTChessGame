package org.portfolio.benttchessgame.chessengine.players;

import org.portfolio.benttchessgame.chessengine.movement.PieceBoardManager;
import org.portfolio.benttchessgame.chessengine.Session;

public class ServerPlayer extends Player{

    //the name will be fetched by the server lobby in combination with client's name.

    public ServerPlayer(String name)
    {
        super(name);
    }

}
