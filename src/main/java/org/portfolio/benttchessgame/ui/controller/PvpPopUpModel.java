package org.portfolio.benttchessgame.ui.controller;

import org.portfolio.benttchessgame.engine.player.Player;

public record PvpPopUpModel(Player whitePlayer, Player blackPlayer, int time, int increment) { }
