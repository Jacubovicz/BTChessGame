package org.portfolio.benttchessgame.engine.command;

import org.portfolio.benttchessgame.engine.player.Player;

public class StartGameCommand implements GameCommand {

    Player p1, p2;
    private final boolean isOfflineGame;

    public StartGameCommand(Player p1, Player p2, boolean isOfflineGame)
    {
        this.p1 = p1;
        this.p2 = p2;

        this.isOfflineGame = isOfflineGame;
    }

    public Player getPlayerOne()
    {
        return p1;
    }

    public Player getPlayerTwo()
    {
        return p2;
    }

    public boolean isOfflineGame()
    {
        return this.isOfflineGame;
    }
}
