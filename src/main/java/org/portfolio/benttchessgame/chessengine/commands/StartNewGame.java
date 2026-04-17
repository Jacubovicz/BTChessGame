package org.portfolio.benttchessgame.chessengine.commands;

import org.portfolio.benttchessgame.chessengine.players.Player;

public class StartNewGame implements EngineCommand{

    Player p1, p2;
    public StartNewGame(Player p1, Player p2)
    {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Player getPlayerOne()
    {
        return p1;
    }

    public Player getPlayerTwo()
    {
        return p2;
    }
}
