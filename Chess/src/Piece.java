import java.awt.*;
import java.util.LinkedList;

public class Piece {
    public static final int PIECE_PAWN = 1;
    public static final int PIECE_KNIGHT = 2;
    public static final int PIECE_BISHOP = 3;
    public static final int PIECE_ROOK = 4;
    public static final int PIECE_QUEEN = 5;
    public static final int PIECE_KING = 6;
    public static final boolean PIECE_WHITE = true;
    public static final boolean PIECE_BLACK = false ;

    int id;
    boolean white;
    int x;
    int y;

    public Piece(int id, boolean white, int x, int y) {
        this.id = id;
        this.white = white;
        this.x = x;
        this.y = y;
    }

    public Point[] getMoves(Board board, boolean futureCheck) {
        LinkedList<Point> moves = new LinkedList<>();

        switch (id) {
            case PIECE_PAWN:
                if (white) {
                    if (board.getPiece(x, y + 1) == null) {
                        moves.add(new Point(x, y + 1));
                        if (y == 2 && board.getPiece(x, 4) == null) { moves.add(new Point(x, 4)); }
                    }
                    if (board.getPiece(x - 1, y + 1) != null && board.getPiece(x - 1, y + 1).white != white) { moves.add(new Point(x - 1, y + 1)); }
                    if (board.getPiece(x + 1, y + 1) != null && board.getPiece(x + 1, y + 1).white != white) { moves.add(new Point(x + 1, y + 1)); }
                    if ((board.whiteEnPassant == x - 1 && y == 5) || (board.whiteEnPassant == x + 1 && y == 5)) { moves.add(new Point(board.whiteEnPassant, 6)); }
                } else {
                    if (board.getPiece(x, y - 1) == null) {
                        moves.add(new Point(x, y - 1));
                        if (y == 7 && board.getPiece(x, 5) == null) { moves.add(new Point(x, 5)); }
                    }
                    if (board.getPiece(x - 1, y - 1) != null && board.getPiece(x - 1, y - 1).white != white) { moves.add(new Point(x - 1, y - 1)); }
                    if (board.getPiece(x + 1, y - 1) != null && board.getPiece(x + 1, y - 1).white != white) { moves.add(new Point(x + 1, y - 1)); }
                    if ((board.blackEnPassant == x - 1 && y == 4) || (board.blackEnPassant == x + 1 && y == 4)) { moves.add(new Point(board.blackEnPassant, 3)); }
                }
                break;
            case PIECE_KNIGHT:
                moves.add(new Point(x - 1, y + 2));
                moves.add(new Point(x + 1, y + 2));
                moves.add(new Point(x + 2, y + 1));
                moves.add(new Point(x + 2, y - 1));
                moves.add(new Point(x + 1, y - 2));
                moves.add(new Point(x - 1, y - 2));
                moves.add(new Point(x - 2, y - 1));
                moves.add(new Point(x - 2, y + 1));
                for (int i = 7; i >= 0; i--) {
                    Point point = moves.get(i);
                    if (board.getPiece(point.x, point.y) != null && board.getPiece(point.x, point.y).white == white) { moves.remove(i); continue; }
                    if (point.x < 1 || point.x > 8 || point.y < 1 || point.y > 8) { moves.remove(i); }
                }
                break;
            case PIECE_BISHOP:
                for (int x2 = x - 1, y2 = y - 1; x2 >= 1 && y2 >= 1; x2--, y2--) {
                    moves.add(new Point(x2, y2));
                    if (board.getPiece(x2, y2) != null) {
                        if (board.getPiece(x2, y2).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int x2 = x + 1, y2 = y - 1; x2 <= 8 && y2 > 0; x2++, y2--) {
                    moves.add(new Point(x2, y2));
                    if (board.getPiece(x2, y2) != null) {
                        if (board.getPiece(x2, y2).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int x2 = x + 1, y2 = y + 1; x2 <= 8 && y2 <= 8; x2++, y2++) {
                    moves.add(new Point(x2, y2));
                    if (board.getPiece(x2, y2) != null) {
                        if (board.getPiece(x2, y2).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int x2 = x - 1, y2 = y + 1; x2 >= 1 && y2 <= 8; x2--, y2++) {
                    moves.add(new Point(x2, y2));
                    if (board.getPiece(x2, y2) != null) {
                        if (board.getPiece(x2, y2).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                break;
            case PIECE_ROOK:
                for (int x2 = x - 1; x2 >= 1; x2--) {
                    moves.add(new Point(x2, y));
                    if (board.getPiece(x2, y) != null) {
                        if (board.getPiece(x2, y).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int x2 = x + 1; x2 <= 8; x2++) {
                    moves.add(new Point(x2, y));
                    if (board.getPiece(x2, y) != null) {
                        if (board.getPiece(x2, y).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int y2 = y - 1; y2 >= 1; y2--) {
                    moves.add(new Point(x, y2));
                    if (board.getPiece(x, y2) != null) {
                        if (board.getPiece(x, y2).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int y2 = y + 1; y2 <= 8; y2++) {
                    moves.add(new Point(x, y2));
                    if (board.getPiece(x, y2) != null) {
                        if (board.getPiece(x, y2).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                break;
            case PIECE_QUEEN:
                for (int x2 = x - 1, y2 = y - 1; x2 >= 1 && y2 >= 1; x2--, y2--) {
                    moves.add(new Point(x2, y2));
                    if (board.getPiece(x2, y2) != null) {
                        if (board.getPiece(x2, y2).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int x2 = x + 1, y2 = y - 1; x2 <= 8 && y2 > 0; x2++, y2--) {
                    moves.add(new Point(x2, y2));
                    if (board.getPiece(x2, y2) != null) {
                        if (board.getPiece(x2, y2).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int x2 = x + 1, y2 = y + 1; x2 <= 8 && y2 <= 8; x2++, y2++) {
                    moves.add(new Point(x2, y2));
                    if (board.getPiece(x2, y2) != null) {
                        if (board.getPiece(x2, y2).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int x2 = x - 1, y2 = y + 1; x2 >= 1 && y2 <= 8; x2--, y2++) {
                    moves.add(new Point(x2, y2));
                    if (board.getPiece(x2, y2) != null) {
                        if (board.getPiece(x2, y2).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int x2 = x - 1; x2 >= 1; x2--) {
                    moves.add(new Point(x2, y));
                    if (board.getPiece(x2, y) != null) {
                        if (board.getPiece(x2, y).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int x2 = x + 1; x2 <= 8; x2++) {
                    moves.add(new Point(x2, y));
                    if (board.getPiece(x2, y) != null) {
                        if (board.getPiece(x2, y).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int y2 = y - 1; y2 >= 1; y2--) {
                    moves.add(new Point(x, y2));
                    if (board.getPiece(x, y2) != null) {
                        if (board.getPiece(x, y2).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                for (int y2 = y + 1; y2 <= 8; y2++) {
                    moves.add(new Point(x, y2));
                    if (board.getPiece(x, y2) != null) {
                        if (board.getPiece(x, y2).white == white) { moves.removeLast(); }
                        break;
                    }
                }
                break;
            case PIECE_KING:
                moves.add(new Point(x - 1, y + 1));
                moves.add(new Point(x, y + 1));
                moves.add(new Point(x + 1, y + 1));
                moves.add(new Point(x + 1, y));
                moves.add(new Point(x + 1, y - 1));
                moves.add(new Point(x, y - 1));
                moves.add(new Point(x - 1, y - 1));
                moves.add(new Point(x - 1, y));
                for (int i = 7; i >= 0; i--) {
                    Point point = moves.get(i);
                    if (board.getPiece(point.x, point.y) != null && board.getPiece(point.x, point.y).white == white) { moves.remove(i); continue; }
                    if (point.x < 1 || point.x > 8 || point.y < 1 || point.y > 8) { moves.remove(i); }
                }
                if (white) {
                    if (board.whiteRightCastle && board.getPiece(7, 1) == null && board.getPiece(6, 1) == null) {
                        moves.add(new Point(7, 1));
                    }
                    if (board.whiteLeftCastle && board.getPiece(2, 1) == null && board.getPiece(3, 1) == null && board.getPiece(4, 1) == null) {
                        moves.add(new Point(3, 1));
                    }
                } else {
                    if (board.blackRightCastle && board.getPiece(7, 8) == null && board.getPiece(6, 8) == null) {
                        moves.add(new Point(7, 8));
                    }
                    if (board.blackLeftCastle && board.getPiece(2, 8) == null && board.getPiece(3, 8) == null && board.getPiece(4, 8) == null) {
                        moves.add(new Point(3, 8));
                    }
                }
                break;
        }

        if (futureCheck) {
            for (int i = moves.size() - 1; i >= 0; i--) {
                if (board.checkFutureCheck(new Point(x, y), moves.get(i))) {
                    moves.remove(i);
                }
            }
        }

        Point[] output = new Point[moves.size()];
        for (int i = 0; i < moves.size(); i++ ) {
            output[i] = moves.get(i);
        }

        return output;
    }
}