package org.portfolio.benttchessgame.engine.player;

public abstract class Player {

   private final String name;
   private boolean color;
   private String profileIconPath;

   public final boolean WHITE = true;
   public final boolean BLACK = false;

   Player(String name) {
       this.name = name;
       this.profileIconPath = "/images/default_player_icon.png";
   }

   public boolean getColor() {
       return this.color;
   }

   public void setColor(boolean color) {
       this.color = color;
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
