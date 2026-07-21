package org.portfolio.benttchessgame.chessengine.players;

import javafx.scene.image.Image;

import java.util.Objects;

public abstract class Player {

   private final String name;
   private boolean color;
   private Image profileIcon;

   public final boolean WHITE = true;
   public final boolean BLACK = false;

   Player(String name)
   {
       this.name = name;
       this.profileIcon = new Image(Objects.requireNonNull(getClass().getResource("/images/default_player_icon.png")).toExternalForm());

   }

   public boolean getColor()
   {
       return this.color;
   }

   public void setColor(boolean color)
   {
       this.color = color;
   }

   public String getName()
   {
       return this.name;
   }

   public Image getProfileIcon()
   {
      return profileIcon;
   }

   public void setProfileIcon(Image image)
   {
      this.profileIcon = image;
   }

}
