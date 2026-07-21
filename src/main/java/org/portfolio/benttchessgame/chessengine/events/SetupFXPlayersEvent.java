package org.portfolio.benttchessgame.chessengine.events;

import org.portfolio.benttchessgame.chessengine.players.Player;

public record SetupFXPlayersEvent(Player player1, Player player2) { }
