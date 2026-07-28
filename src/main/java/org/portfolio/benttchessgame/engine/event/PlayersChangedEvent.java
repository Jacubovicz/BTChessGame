package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.engine.player.Player;

public record PlayersChangedEvent(Player player1, Player player2) { }
