import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class Board {
    public static final int STATE_REGULAR = 1;
    public static final int STATE_CHECKMATE = 2;
    public static final int STATE_DRAW = 3;
    public HashMap<Point, Piece> pieces = new HashMap<>();
    public Point selectedPiece = null;
    public int whiteEnPassant = -1;
    public int blackEnPassant = -1;
    public int gameState = 1;
    public boolean whiteMove = true;
    public boolean lastMouse = false;
    public boolean whiteRightCastle = true;
    public boolean whiteLeftCastle = true;
    public boolean blackRightCastle = true;
    public boolean blackLeftCastle = true;



    public void tick(WindowManager windowManager) {
        if (!windowManager.mouseDown && lastMouse) {
            click(windowManager);
        }
        lastMouse = windowManager.mouseDown;

        BufferStrategy strategy = windowManager.window.getBufferStrategy();

        do {
            do {
                Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

                switch (gameState) {
                    case STATE_REGULAR:
                        windowManager.clear(g, Color.BLACK);
                        drawFulLBoard(g, windowManager);
                        if (pieces.get(selectedPiece) != null && pieces.get(selectedPiece).white == whiteMove) { drawValidMoves(g, windowManager, pieces.get(selectedPiece)); }
                        break;
                    case STATE_CHECKMATE:
                        windowManager.clear(g, Color.BLACK);
                        drawFulLBoard(g, windowManager);
                        windowManager.drawSquare(g, 0, 225, 500, 75, Color.WHITE);
                        String winText = "Checkmate by White!";
                        if (!whiteMove) { winText = "Checkmate by Black!"; };
                        windowManager.drawText(g, 250, 250, 50, Font.TRUETYPE_FONT, Font.SANS_SERIF, winText, Color.BLACK, true);
                        break;
                    case STATE_DRAW:
                        windowManager.clear(g, Color.BLACK);
                        drawFulLBoard(g, windowManager);
                        windowManager.drawSquare(g, 0, 225, 500, 75, Color.WHITE);
                        windowManager.drawText(g, 250, 250, 50, Font.TRUETYPE_FONT, Font.SANS_SERIF, "Draw!", Color.BLACK, true);
                        break;
                }

                g.dispose();
            } while (strategy.contentsRestored());
            strategy.show();
        } while (strategy.contentsLost());
    }

    public void click(WindowManager windowManager) {
        int mouseX = MouseInfo.getPointerInfo().getLocation().x - windowManager.window.getLocation().x;
        int mouseY = MouseInfo.getPointerInfo().getLocation().y - windowManager.window.getLocation().y;
        Point mousePos = new Point(mouseX, mouseY);
        if (whiteMove) { mousePos = new Point(mouseX, 500 - mouseY); }
        Point clickPos = new Point((int) Math.floor(mousePos.x / 62f + 1), (int) Math.floor(mousePos.y / 62f + 1));
        Point pawnPromotion = pawnPromotionPosition();

        if (pawnPromotion != null) {
            if (clickPos.x == pawnPromotion.x) {
                int id = -1;
                if (whiteMove) {
                    id = switch (clickPos.y) {
                        case 8 -> Piece.PIECE_KNIGHT;
                        case 7 -> Piece.PIECE_BISHOP;
                        case 6 -> Piece.PIECE_ROOK;
                        case 5 -> Piece.PIECE_QUEEN;
                        default -> id;
                    };
                    if (id != -1) {
                        createPiece(id, Piece.PIECE_WHITE, pawnPromotion.x, pawnPromotion.y);
                        whiteMove = !whiteMove;
                    }
                } else {
                    id = switch (clickPos.y) {
                        case 1 -> Piece.PIECE_KNIGHT;
                        case 2 -> Piece.PIECE_BISHOP;
                        case 3 -> Piece.PIECE_ROOK;
                        case 4 -> Piece.PIECE_QUEEN;
                        default -> id;
                    };
                    if (id != -1) {
                        createPiece(id, Piece.PIECE_BLACK, pawnPromotion.x, pawnPromotion.y);
                        whiteMove = !whiteMove;
                    }
                }
            }
            return;
        }

        Piece clicked = pieces.get(selectedPiece);
        if (clicked != null && clicked.white == whiteMove) {
            Point[] validMoves = clicked.getMoves(this, true);
            for (Point i : validMoves) {
                if (i.equals(clickPos)) {
                    if (whiteMove) {
                        if (clickPos.x == whiteEnPassant && clickPos.y == 6) { deletePiece(clickPos.x, clickPos.y - 1); }
                        whiteEnPassant = -1;
                    }
                    else {
                        if (clickPos.x == blackEnPassant && clickPos.y == 3) { deletePiece(clickPos.x, clickPos.y + 1); }
                        blackEnPassant = -1;
                    }

                    movePiece(selectedPiece, clickPos, false);
                    if (clicked.id == Piece.PIECE_KING) {
                        int change = clicked.x - clickPos.x;
                        if (whiteMove) {
                            if (change == 2) { movePiece(new Point(1, 1), new Point(4, 1), false); }
                            else if (change == -2) { movePiece(new Point(8, 1), new Point(6, 1), false); }
                        } else {
                            if (change == 2) { movePiece(new Point(1, 8), new Point(4, 8), false); }
                            else if (change == -2) { movePiece(new Point(8, 8), new Point(6, 8), false); }
                        }
                    } else if (clicked.id == Piece.PIECE_PAWN) {
                        int changeY = Math.abs(clicked.y - clickPos.y);
                        if (whiteMove) {
                            if (changeY == 2) { blackEnPassant = clicked.x; }
                        }
                        else {
                            if (changeY == 2) { whiteEnPassant = clicked.x; }
                        }
                    }
                    selectedPiece = null;
                    if (pawnPromotionPosition() == null) { whiteMove = !whiteMove; }

                    gameState = getGameState();
                    if (gameState != 1) { whiteMove = !whiteMove; }

                    return;
                }
            }
        }

        selectedPiece = clickPos;
    }

    public boolean checkIfInCheck() {
        Point friendlyKing = new Point();
        for (Piece i : pieces.values()) {
            if (i.id == Piece.PIECE_KING && i.white == whiteMove) { friendlyKing = new Point(i.x, i.y);  }
        }
        for (Piece x : pieces.values()) {
            Point[] validMoves = x.getMoves(this, false);
            for (Point y : validMoves) {
                if (friendlyKing.equals(y)) { return true; }
            }
        }
        return false;
    }

    public boolean checkFutureCheck(Point pieceToMove, Point positionToMove) {
        Piece storage = pieces.get(positionToMove);
        movePiece(pieceToMove, positionToMove, true);
        boolean output = checkIfInCheck();
        movePiece(positionToMove, pieceToMove, true);
        if (storage != null) { createPiece(storage.id, storage.white, storage.x, storage.y); }
        return output;
    }

    public void drawBoard(Graphics2D g, WindowManager windowManager) {
        boolean alternate = true;
        if (whiteMove) { alternate = false; }
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (alternate) {
                    windowManager.drawSquare(g, 62 * r, 62 * c, 62, 62, Color.DARK_GRAY);
                } else {
                    windowManager.drawSquare(g, 62 * r, 62 * c, 62, 62, Color.LIGHT_GRAY);
                }
                alternate = !alternate;
            }
            alternate = !alternate;
        }
    }

    public void drawPieces(Graphics2D g, WindowManager windowManager) {
        for (Piece i : pieces.values()) {
            BufferedImage img = null;
            switch (i.id) {
                case Piece.PIECE_PAWN:
                    if (i.white) { img = Resource.getImage("White-Pawn.png"); }
                    else { img = Resource.getImage("Black-Pawn.png"); }
                    break;
                case Piece.PIECE_KNIGHT:
                    if (i.white) { img = Resource.getImage("White-Knight.png"); }
                    else { img = Resource.getImage("Black-Knight.png"); }
                    break;
                case Piece.PIECE_BISHOP:
                    if (i.white) { img = Resource.getImage("White-Bishop.png"); }
                    else { img = Resource.getImage("Black-Bishop.png"); }
                    break;
                case Piece.PIECE_ROOK:
                    if (i.white) { img = Resource.getImage("White-Rook.png"); }
                    else { img = Resource.getImage("Black-Rook.png"); }
                    break;
                case Piece.PIECE_QUEEN:
                    if (i.white) { img = Resource.getImage("White-Queen.png"); }
                    else { img = Resource.getImage("Black-Queen.png"); }
                    break;
                case Piece.PIECE_KING:
                    if (i.white) { img = Resource.getImage("White-King.png"); }
                    else { img = Resource.getImage("Black-King.png"); }
                    break;
            }
            if (whiteMove) { windowManager.drawImage(g, (i.x - 1) * 62, 438 - (i.y - 1) * 62, 62, 62, img); }
            else { windowManager.drawImage(g, (i.x - 1) * 62, (i.y - 1) * 62, 62, 62, img); }
        }
    }

    public void drawFulLBoard(Graphics2D g, WindowManager windowManager) {
        drawBoard(g, windowManager);
        drawPieces(g, windowManager);
        drawPawnPromotionMenu(g, windowManager);
    }

    public void drawValidMoves(Graphics2D g, WindowManager windowManager, Piece piece) {
        Point[] moves = piece.getMoves(this, true);
        if (whiteMove) { for (Point i : moves) { windowManager.drawSphere(g, (i.x - 1) * 62 + 16, 438 - (i.y - 1) * 62 + 16, 31, 31, Color.YELLOW); } }
        else { for (Point i : moves) { windowManager.drawSphere(g, (i.x - 1) * 62 + 16, (i.y - 1) * 62 + 16, 31, 31, Color.YELLOW); } }
    }

    public void createPiece(int id, boolean white, int x, int y) {
        pieces.put(new Point(x, y), new Piece(id, white, x, y));
    }

    public Piece getPiece(int x, int y) {
        return pieces.get(new Point(x, y));
    }

    public void deletePiece(int x, int y) {
        pieces.remove(new Point(x, y));
    }

    public void movePiece(Point selectedPiece, Point location, boolean testMove) {
        Piece piece = pieces.get(selectedPiece);
        if (!testMove) {
            if (whiteMove) {
                if (piece.id == Piece.PIECE_KING) {
                    whiteLeftCastle = false;
                    whiteRightCastle = false;
                } else if (piece.id == Piece.PIECE_ROOK) {
                    if (piece.x == 1) {
                        whiteLeftCastle = false;
                    } else if (piece.x == 8) {
                        whiteRightCastle = false;
                    }
                }
            } else {
                if (piece.id == Piece.PIECE_KING) {
                    blackLeftCastle = false;
                    blackRightCastle = false;
                } else if (piece.id == Piece.PIECE_ROOK) {
                    if (piece.x == 1) {
                        blackLeftCastle = false;
                    } else if (piece.x == 8) {
                        blackRightCastle = false;
                    }
                }
            }
        }
        createPiece(piece.id, piece.white, location.x, location.y);
        pieces.remove(selectedPiece);
    }

    public Point pawnPromotionPosition() {
        for (Piece i : pieces.values()) { if (i.id == Piece.PIECE_PAWN && (i.y == 8 && i.white || i.y == 1 && !i.white)) { return new Point(i.x, i.y); } }
        return null;
    }

    public void drawPawnPromotionMenu(Graphics2D g, WindowManager windowManager) {
        Point pawnPosition = pawnPromotionPosition();
        if (pawnPosition != null) {
            windowManager.drawSquare(g, (pawnPosition.x - 1) * 62,  0, 62, 248, Color.WHITE);
            if (whiteMove) {
                windowManager.drawImage(g, (pawnPosition.x - 1) * 62, 0, 62, 62, Resource.getImage("White-Knight.png"));
                windowManager.drawImage(g, (pawnPosition.x - 1) * 62, 62, 62, 62, Resource.getImage("White-Bishop.png"));
                windowManager.drawImage(g, (pawnPosition.x - 1) * 62, 124, 62, 62, Resource.getImage("White-Rook.png"));
                windowManager.drawImage(g, (pawnPosition.x - 1) * 62, 186, 62,  62, Resource.getImage("White-Queen.png"));
            } else {
                windowManager.drawImage(g, (pawnPosition.x - 1) * 62, 0, 62, 62, Resource.getImage("Black-Knight.png"));
                windowManager.drawImage(g, (pawnPosition.x - 1) * 62, 62, 62, 62, Resource.getImage("Black-Bishop.png"));
                windowManager.drawImage(g, (pawnPosition.x - 1) * 62, 124, 62, 62, Resource.getImage("Black-Rook.png"));
                windowManager.drawImage(g, (pawnPosition.x - 1) * 62, 186, 62, 62, Resource.getImage("Black-Queen.png"));
            }
        }
    }

    public int getGameState() {
        LinkedList<Piece> allPieces = new LinkedList<>(pieces.values());

        if (allPieces.size() == 2) { return STATE_DRAW; }

        for (Piece i : allPieces) {
            Point[] validMoves = i.getMoves(this, true);
            if (validMoves.length != 0 && i.white == whiteMove) { return STATE_REGULAR; }
        }
        if (checkIfInCheck()) {
            return STATE_CHECKMATE;
        } else {
            return STATE_DRAW;
        }

        /*
        I don't know why but running i.getMoves(this, true) throws an error when using pieces.value() directly.
        for (Piece i : pieces.values()) {
            Point[] validMoves = i.getMoves(this, true);
            if (validMoves.length != 0) { return STATE_REGULAR; }
        }
        */
    }

    public void setBoard() {
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_WHITE, 1, 2);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_WHITE, 2, 2);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_WHITE, 3, 2);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_WHITE, 4, 2);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_WHITE, 5, 2);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_WHITE, 6, 2);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_WHITE, 7, 2);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_WHITE, 8, 2);
        createPiece(Piece.PIECE_KNIGHT, Piece.PIECE_WHITE, 2, 1);
        createPiece(Piece.PIECE_KNIGHT, Piece.PIECE_WHITE, 7, 1);
        createPiece(Piece.PIECE_BISHOP, Piece.PIECE_WHITE, 3, 1);
        createPiece(Piece.PIECE_BISHOP, Piece.PIECE_WHITE, 6, 1);
        createPiece(Piece.PIECE_ROOK, Piece.PIECE_WHITE, 1, 1);
        createPiece(Piece.PIECE_ROOK, Piece.PIECE_WHITE, 8, 1);
        createPiece(Piece.PIECE_QUEEN, Piece.PIECE_WHITE, 4, 1);
        createPiece(Piece.PIECE_KING, Piece.PIECE_WHITE, 5, 1);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_BLACK, 1, 7);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_BLACK, 2, 7);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_BLACK, 3, 7);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_BLACK, 4, 7);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_BLACK, 5, 7);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_BLACK, 6, 7);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_BLACK, 7, 7);
        createPiece(Piece.PIECE_PAWN, Piece.PIECE_BLACK, 8, 7);
        createPiece(Piece.PIECE_KNIGHT, Piece.PIECE_BLACK, 2, 8);
        createPiece(Piece.PIECE_KNIGHT, Piece.PIECE_BLACK, 7, 8);
        createPiece(Piece.PIECE_BISHOP, Piece.PIECE_BLACK, 3, 8);
        createPiece(Piece.PIECE_BISHOP, Piece.PIECE_BLACK, 6, 8);
        createPiece(Piece.PIECE_ROOK, Piece.PIECE_BLACK, 1, 8);
        createPiece(Piece.PIECE_ROOK, Piece.PIECE_BLACK, 8, 8);
        createPiece(Piece.PIECE_QUEEN, Piece.PIECE_BLACK, 4, 8);
        createPiece(Piece.PIECE_KING, Piece.PIECE_BLACK, 5, 8);
    }
}