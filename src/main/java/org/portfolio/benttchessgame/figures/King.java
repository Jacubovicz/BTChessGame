package org.portfolio.benttchessgame.figures;

public class King extends Figure{



    private final int[][] offsets = {{-1,-1}, {0,-1}, {1,-1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

    private boolean moved = false;

    public King(boolean color) {
        super(color);
        super.setName("king");
    }

    @Override
    public int[][] getOffset() {

        return offsets;
    }

    public void setMoved()
    {
        this.moved = true;
    }

    public boolean getMoved()
    {
        return this.moved;
    }

}
