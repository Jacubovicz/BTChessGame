package org.portfolio.benttchessgame.chessengine.players;

import org.portfolio.benttchessgame.chessengine.MoveOperation;

public class ServerPlayer extends Player{

    //the name will be fetched by the server lobby in combination with client's name.

    public ServerPlayer(String name)
    {
        super(name);
    }

    @Override
    public void publishMove(MoveOperation operation) {

    }

}
