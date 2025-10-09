import java.util.*;

/**
 * Console Chess in Java (OOP)
 * ------------------------------------------------------------
 * - Play in terminal with coordinate moves like: e2 e4, g8 f6
 * - Validates legal moves (no leaving king in check)
 * - Detects check, checkmate, stalemate, and basic 50-move draw counter
 * - Pawn promotion to Queen (auto) when reaching back rank
 * - Skips advanced rules (castling, en passant, threefold repetition, clocks)
 *
 * Compile & Run:
 *   javac Chess.java && java Chess
 */
public class ChessExample {
    public static void main(String[] args) {
        new Game().play();
    }
}

// ===== Core Game Engine =====
class Game {
    private final Board board = new Board();
    private Color sideToMove = Color.WHITE;
    private int halfmoveClock = 0; // simple 50-move rule counter (reset on pawn move or capture)

    public void play() {
        board.setupInitial();
        Scanner sc = new Scanner(System.in);
        println("\nWelcome to Console Chess! Type moves like: e2 e4  (or)  e7e5");
        println("Type 'help' for commands.\n");
        while (true) {
            board.print(sideToMove);
            if (board.isCheckmate(sideToMove)) {
                println("Checkmate! " + sideToMove.opposite() + " wins.");
                break;
            }
            if (board.isStalemate(sideToMove)) {
                println("Stalemate! Draw.");
                break;
            }
            if (halfmoveClock >= 100) { // 50 full moves == 100 ply
                println("Draw by 50-move rule.");
                break;
            }

            if (board.isInCheck(sideToMove)) {
                println("(" + sideToMove + ") You are in CHECK!");
            }

            System.out.print(sideToMove + " to move > ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) break;
            if (line.equalsIgnoreCase("help")) { printHelp(); continue; }
            if (line.equalsIgnoreCase("show")) { board.print(sideToMove); continue; }
            if (line.equalsIgnoreCase("resign")) { println(sideToMove + " resigns. " + sideToMove.opposite() + " wins."); break; }

            Optional<Move> opt = InputParser.parseMove(line);
            if (opt.isEmpty()) { println("Couldn't parse. Try like: e2 e4"); continue; }
            Move m = opt.get();

            List<Move> legal = board.generateLegalMoves(sideToMove);
            Optional<Move> chosen = legal.stream().filter(m::equalsWithoutNote).findFirst();
            if (chosen.isEmpty()) {
                println("Illegal move.");
                continue;
            }
            Move exec = chosen.get();

            // Execute
            Piece moved = board.get(exec.from);
            Piece captured = board.get(exec.to);
            board.makeMove(exec);

            // Update 50-move rule clock
            if (moved instanceof Pawn || captured != null) halfmoveClock = 0; else halfmoveClock++;

            // Auto-queen promotion when a pawn reaches back rank
            if (moved instanceof Pawn && (exec.to.rank == 0 || exec.to.rank == 7)) {
                board.set(exec.to, new Queen(moved.color));
            }

            sideToMove = sideToMove.opposite();
        }
    }

    private void printHelp() {
        println("Commands: \n  - Enter a move: e2 e4  or e7e5\n  - show : reprint the board\n  - resign : resign the game\n  - help : this help\n  - quit/exit : quit the game\n\nNotes:\n  * Castling and en passant are not implemented.\n  * Pawn promotion auto-queens.\n");
    }

    private static void println(String s) { System.out.println(s); }
}

// ===== Board, Pieces, Moves =====
class Board {
    private final Piece[][] grid = new Piece[8][8]; // [rank][file], rank 0 = 8th rank (Black back rank)

    public Piece get(Position p) { return grid[p.rank][p.file]; }
    public void set(Position p, Piece piece) { grid[p.rank][p.file] = piece; }

    public void setupInitial() {
        // Clear
        for (int r=0;r<8;r++) Arrays.fill(grid[r], null);
        // Pawns
        for (int f=0; f<8; f++) {
            set(Position.of(6, f), new Pawn(Color.WHITE)); // rank 2 in chess view
            set(Position.of(1, f), new Pawn(Color.BLACK)); // rank 7 in chess view
        }
        // Rooks
        set(Position.of(7,0), new Rook(Color.WHITE)); set(Position.of(7,7), new Rook(Color.WHITE));
        set(Position.of(0,0), new Rook(Color.BLACK)); set(Position.of(0,7), new Rook(Color.BLACK));
        // Knights
        set(Position.of(7,1), new Knight(Color.WHITE)); set(Position.of(7,6), new Knight(Color.WHITE));
        set(Position.of(0,1), new Knight(Color.BLACK)); set(Position.of(0,6), new Knight(Color.BLACK));
        // Bishops
        set(Position.of(7,2), new Bishop(Color.WHITE)); set(Position.of(7,5), new Bishop(Color.WHITE));
        set(Position.of(0,2), new Bishop(Color.BLACK)); set(Position.of(0,5), new Bishop(Color.BLACK));
        // Queens
        set(Position.of(7,3), new Queen(Color.WHITE));
        set(Position.of(0,3), new Queen(Color.BLACK));
        // Kings
        set(Position.of(7,4), new King(Color.WHITE));
        set(Position.of(0,4), new King(Color.BLACK));
    }

    public void print(Color perspective) {
        boolean whitePerspective = (perspective == Color.WHITE);
        int startRank = whitePerspective ? 7 : 0;
        int endRank   = whitePerspective ? -1 : 8;
        int stepRank  = whitePerspective ? -1 : 1;
        int startFile = whitePerspective ? 0 : 7;
        int endFile   = whitePerspective ? 8 : -1;
        int stepFile  = whitePerspective ? 1 : -1;

        System.out.println("  +------------------------+");
        for (int r = startRank; r != endRank; r += stepRank) {
            System.out.print((r+1) + " | ");
            for (int f = startFile; f != endFile; f += stepFile) {
                Piece p = grid[r][f];
                String s = (p == null) ? "." : String.valueOf(p.fen());

                s = s.replace("P","♙").replace("p","♟")
                        .replace("R","♖").replace("r","♜")
                        .replace("N","♘").replace("n","♞")
                        .replace("B","♗").replace("b","♝")
                        .replace("Q","♕").replace("q","♛")
                        .replace("K","♔").replace("k","♚");

                System.out.print(s + " ");

            }
            System.out.println("|");
        }
        System.out.println("  +------------------------+");
        if (whitePerspective) System.out.println("    a b c d e f g h\n");
        else System.out.println("    h g f e d c b a\n");
    }

    public void makeMove(Move m) {
        Piece p = get(m.from);
        set(m.to, p);
        set(m.from, null);
    }

    public boolean isInside(int r, int f) { return r>=0 && r<8 && f>=0 && f<8; }

    public boolean isEmpty(Position p) { return get(p)==null; }

    public boolean isOpponent(Position p, Color side) {
        Piece x = get(p); return x != null && x.color != side;
    }

    public boolean isPathClear(Position from, Position to, int dr, int df) {
        int r = from.rank + dr, f = from.file + df;
        while (r != to.rank || f != to.file) {
            if (!isInside(r,f)) return false;
            if (grid[r][f] != null) return false;
            r += dr; f += df;
        }
        return true;
    }

    public Position findKing(Color c) {
        for (int r=0;r<8;r++) for (int f=0;f<8;f++) {
            Piece p = grid[r][f];
            if (p instanceof King && p.color==c) return Position.of(r,f);
        }
        return null; // shouldn't happen in a normal game
    }

    public boolean isSquareAttacked(Position sq, Color bySide) {
        // Generate pseudo-legal attacks for all pieces of bySide and see if any hit sq
        for (int r=0;r<8;r++) for (int f=0;f<8;f++) {
            Piece p = grid[r][f];
            if (p==null || p.color!=bySide) continue;
            Position from = Position.of(r,f);
            for (Position t : p.pseudoTargets(this, from, true)) { // true = attack mode (pawns differ)
                if (t.equals(sq)) return true;
            }
        }
        return false;
    }

    public boolean isInCheck(Color side) {
        Position k = findKing(side);
        return isSquareAttacked(k, side.opposite());
    }

    public Board copy() {
        Board b = new Board();
        for (int r=0;r<8;r++) for (int f=0;f<8;f++) {
            Piece p = grid[r][f];
            if (p!=null) b.grid[r][f] = p.copy();
        }
        return b;
    }

    public List<Move> generateLegalMoves(Color side) {
        List<Move> moves = new ArrayList<>();
        for (int r=0;r<8;r++) for (int f=0;f<8;f++) {
            Piece p = grid[r][f];
            if (p==null || p.color!=side) continue;
            Position from = Position.of(r,f);
            for (Position to : p.pseudoTargets(this, from, false)) {
                Move m = new Move(from, to);
                if (isLegal(m, side)) moves.add(m);
            }
        }
        return moves;
    }

    public boolean isLegal(Move m, Color side) {
        Piece p = get(m.from);
        if (p==null || p.color!=side) return false;
        // ensure destination is pseudo-legal
        boolean found = false;
        for (Position t : p.pseudoTargets(this, m.from, false)) if (t.equals(m.to)) { found = true; break; }
        if (!found) return false;

        Board b2 = this.copy();
        b2.makeMove(m);
        return !b2.isInCheck(side);
    }

    public boolean isCheckmate(Color side) {
        return isInCheck(side) && generateLegalMoves(side).isEmpty();
    }

    public boolean isStalemate(Color side) {
        return !isInCheck(side) && generateLegalMoves(side).isEmpty();
    }
}

class Move {
    public final Position from, to;
    public Move(Position from, Position to) { this.from = from; this.to = to; }
    @Override public String toString() { return from + "->" + to; }

    public boolean equalsWithoutNote(Move other) { return this.from.equals(other.from) && this.to.equals(other.to); }
}

enum Color { WHITE, BLACK; public Color opposite(){ return this==WHITE?BLACK:WHITE; } }

final class Position {
    public final int rank; // 0..7 (0=8th rank), internal top-down
    public final int file; // 0..7 (a..h)
    private Position(int r, int f){ this.rank=r; this.file=f; }
    public static Position of(int r, int f){ return new Position(r,f); }
    public static Optional<Position> fromAlgebraic(String s) {
        if (s==null || s.length()!=2) return Optional.empty();
        char f = Character.toLowerCase(s.charAt(0));
        char r = s.charAt(1);
        if (f<'a' || f>'h' || r<'1' || r>'8') return Optional.empty();
        int file = f - 'a';
        int rankHuman = r - '1'; // 0..7 bottom-up
        int rank = 7 - rankHuman; // convert to internal 0=top
        return Optional.of(of(rank, file));
    }
    @Override public String toString(){ return ""+(char)('a'+file)+(8-rank); }
    @Override public boolean equals(Object o){ if(!(o instanceof Position)) return false; Position p=(Position)o; return p.rank==rank && p.file==file; }
    @Override public int hashCode(){ return Objects.hash(rank,file); }
}

abstract class Piece {
    public final Color color;
    public Piece(Color c){ this.color=c; }
    public abstract List<Position> pseudoTargets(Board b, Position from, boolean attackOnly);
    public abstract char fen();
    public abstract Piece copy();

    protected void addIfEmptyOrCapture(Board b, List<Position> acc, Position to) {
        if (!b.isInside(to.rank,to.file)) return;
        Piece q = b.get(to);
        if (q==null || q.color!=this.color) acc.add(to);
    }
}

class Pawn extends Piece {
    public Pawn(Color c){ super(c); }
    @Override public List<Position> pseudoTargets(Board b, Position from, boolean attackOnly) {
        List<Position> t = new ArrayList<>();
        int dir = (color==Color.WHITE) ? -1 : 1; // white goes up (towards rank 0)
        int startRank = (color==Color.WHITE) ? 6 : 1;

        // captures (used both in normal and attackOnly modes)
        int[][] caps = {{dir, -1}, {dir, 1}};
        for (int[] d : caps) {
            int r = from.rank + d[0], f = from.file + d[1];
            if (!b.isInside(r,f)) continue;
            Position to = Position.of(r,f);
            if (attackOnly) t.add(to);
            else if (b.isOpponent(to, color)) t.add(to);
        }

        if (attackOnly) return t; // in attack mode, skip forward pushes

        // single push
        int r1 = from.rank + dir;
        if (b.isInside(r1, from.file) && b.isEmpty(Position.of(r1, from.file))) {
            t.add(Position.of(r1, from.file));
            // double push
            int r2 = from.rank + 2*dir;
            if (from.rank == startRank && b.isEmpty(Position.of(r2, from.file))) {
                t.add(Position.of(r2, from.file));
            }
        }
        // (No en passant in this simplified version)
        return t;
    }
    @Override public char fen(){ return color==Color.WHITE? 'P':'p'; }
    @Override public Piece copy(){ return new Pawn(color); }
}

class Knight extends Piece {
    public Knight(Color c){ super(c); }
    private static final int[][] JUMPS = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
    @Override public List<Position> pseudoTargets(Board b, Position from, boolean attackOnly) {
        List<Position> t = new ArrayList<>();
        for (int[] d : JUMPS) {
            int r=from.rank+d[0], f=from.file+d[1];
            if (!b.isInside(r,f)) continue;
            addIfEmptyOrCapture(b, t, Position.of(r,f));
        }
        return t;
    }
    @Override public char fen(){ return color==Color.WHITE? 'N':'n'; }
    @Override public Piece copy(){ return new Knight(color); }
}

class Bishop extends Piece {
    public Bishop(Color c){ super(c); }
    @Override public List<Position> pseudoTargets(Board b, Position from, boolean attackOnly) {
        return slide(b, from, new int[][]{{-1,-1},{-1,1},{1,-1},{1,1}});
    }
    protected List<Position> slide(Board b, Position from, int[][] dirs) {
        List<Position> t = new ArrayList<>();
        for (int[] d : dirs) {
            int r = from.rank + d[0], f = from.file + d[1];
            while (b.isInside(r,f)) {
                Position to = Position.of(r,f);
                Piece q = b.get(to);
                if (q==null) { t.add(to); }
                else { if (q.color!=this.color) t.add(to); break; }
                r += d[0]; f += d[1];
            }
        }
        return t;
    }
    @Override public char fen(){ return color==Color.WHITE? 'B':'b'; }
    @Override public Piece copy(){ return new Bishop(color); }
}

class Rook extends Piece {
    public Rook(Color c){ super(c); }
    @Override public List<Position> pseudoTargets(Board b, Position from, boolean attackOnly) {
        return slide(b, from, new int[][]{{-1,0},{1,0},{0,-1},{0,1}});
    }
    protected List<Position> slide(Board b, Position from, int[][] dirs) {
        List<Position> t = new ArrayList<>();
        for (int[] d : dirs) {
            int r = from.rank + d[0], f = from.file + d[1];
            while (b.isInside(r,f)) {
                Position to = Position.of(r,f);
                Piece q = b.get(to);
                if (q==null) { t.add(to); }
                else { if (q.color!=this.color) t.add(to); break; }
                r += d[0]; f += d[1];
            }
        }
        return t;
    }
    @Override public char fen(){ return color==Color.WHITE? 'R':'r'; }
    @Override public Piece copy(){ return new Rook(color); }
}

class Queen extends Piece {
    public Queen(Color c){ super(c); }
    @Override public List<Position> pseudoTargets(Board b, Position from, boolean attackOnly) {
        List<Position> t = new ArrayList<>();
        t.addAll(new Rook(color).pseudoTargets(b, from, attackOnly));
        t.addAll(new Bishop(color).pseudoTargets(b, from, attackOnly));
        return t;
    }
    @Override public char fen(){ return color==Color.WHITE? 'Q':'q'; }
    @Override public Piece copy(){ return new Queen(color); }
}

class King extends Piece {
    public King(Color c){ super(c); }
    private static final int[][] STEPS = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
    @Override public List<Position> pseudoTargets(Board b, Position from, boolean attackOnly) {
        List<Position> t = new ArrayList<>();
        for (int[] d : STEPS) {
            int r=from.rank+d[0], f=from.file+d[1];
            if (!b.isInside(r,f)) continue;
            Position to = Position.of(r,f);
            Piece q = b.get(to);
            if (q==null || q.color!=this.color) t.add(to);
        }
        // (Castling not implemented in this simplified version)
        return t;
    }
    @Override public char fen(){ return color==Color.WHITE? 'K':'k'; }
    @Override public Piece copy(){ return new King(color); }
}

// ===== Input Parser =====
class InputParser {
    /** Accepts formats like: "e2 e4" or "e2e4" (case-insensitive) */
    public static Optional<Move> parseMove(String raw) {
        if (raw == null || raw.isEmpty()) return Optional.empty();
        raw = raw.trim().toLowerCase().replaceAll("\\s+", "");
        if (raw.length()!=4) return Optional.empty();
        Optional<Position> a = Position.fromAlgebraic(raw.substring(0,2));
        Optional<Position> b = Position.fromAlgebraic(raw.substring(2,4));
        if (a.isEmpty() || b.isEmpty()) return Optional.empty();
        return Optional.of(new Move(a.get(), b.get()));
    }
}
