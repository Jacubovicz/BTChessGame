package org.portfolio.benttchessgame.engine.player;

public abstract class Player {

   private final String name;
   private PlayerColor playerColor;
   private String profileIconPath;

   public final boolean WHITE = true;
   public final boolean BLACK = false;

   Player(String name) {
       this.name = name;
       this.profileIconPath = "/images/gameview/default_player_icon.png";
   }

   public PlayerColor getColor() {
       return this.playerColor;
   }

   public void setColor(PlayerColor color) {
       this.playerColor = color;
   }

   public String getName() {
       return this.name;
   }

   public String getProfileIconPath() {
      return profileIconPath;
   }

   public void setProfileIconPath(String path) {
      this.profileIconPath = path;
   }
}
