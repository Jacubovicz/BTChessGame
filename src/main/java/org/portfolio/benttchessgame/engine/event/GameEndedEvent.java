package org.portfolio.benttchessgame.engine.event;


import org.portfolio.benttchessgame.engine.player.PlayerColor;

public record GameEndedEvent(String message, PlayerColor winnerColor, int totalMovePerformed, String winningPlayerName) {}
