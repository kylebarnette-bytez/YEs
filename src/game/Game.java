package game;

import board.Board;

public class Game {
    private Board board;
    private String currentTurn; // "white" or "black"

    public void start() {
        board = new Board();
        currentTurn = "white";
        // TODO: game loop with user input
    }
}
