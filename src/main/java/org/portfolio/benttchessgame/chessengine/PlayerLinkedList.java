package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.players.Player;

public class PlayerLinkedList {

    public static class Node
    {
        Player data;
        Node next;

        Node(Player player)
        {
            this.data = player;
        }

        public Player getPlayer()
        {
            if(data != null)
            {
                return data;
            }
            return null;
        }
    }

    Node head = null, tail=null;
    Node currentNode = null;

    public PlayerLinkedList(Player player1, Player player2)
    {
        this.head = new Node(player1);
        this.tail = new Node(player2);

        head.next = tail;
        tail.next = head;

        this.currentNode = head;
    }

    public Player next()
    {
        this.currentNode = currentNode.next;
        return currentNode.data;
    }

   public Player getCurrentPlayer()
   {
       if(currentNode != null)
       {
           return currentNode.data;
       }
       return null;
   }
}
