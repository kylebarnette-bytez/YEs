package pieces;

import board.Position;
import java.util.List;

public abstract class Piece {
    protected String color;        // "white" or "black"
    protected Position position;   // current location on the board

    public Piece(String color, Position position) {
        this.color = color;
        this.position = position;
    }

    public String getColor() { return color; }
    public Position getPosition() { return position; }
    public void setPosition(Position pos) { this.position = pos; }

    // Abstract: each subclass must define its move logic
    public abstract List<Position> possibleMoves();
}
