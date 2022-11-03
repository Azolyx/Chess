
# Chess

## Description

This is chess coded in pure java. It follows all the regular rules of chess. The board automaticly sets itself up when you start the app, after each move the board will flip letting the other person play a move. It is meant to be played by 2 people on 1 computer.

## How to play

Follow the link below to get a pdf of the official rules of chess.

> [Full official chess rules](https://www.fide.com/FIDE/handbook/LawsOfChess.pdf)

## The Code

### Basic Changes

#### Change Pieces

The board setup can be found in the Board class in the setBoard() function at the bottom. You can alter this to change what pieces start in the game. To add a new piece simply run the createPiece() function that takes an input of the piece ID (Found in the static variables of the Piece class), if the piece is white or not (Also found in the static variables in the piece class), and then an x and y variables (From 1 - 8).

#### Create new piece

To create a new piece go into the Piece class and add a new static variable of the piece name, and have the value be the piece ID. Example:

`public static final int PIECE_PAWN = 1;`

Then go into the getMoves() function and add a new statement to the switch case using your new static variable. Then inside the switch case the moves you want the piece to be able to do add to the moves variable. Example:

    case PIECE_PAWN:
        if (white) {
            if (board.getPiece(x, y + 1) == null) {
                moves.add(new Point(x, y + 1));
                if (y == 2 && board.getPiece(x, 4) == null) { moves.add(new Point(x, 4)); }
            }
            if (board.getPiece(x - 1, y + 1) != null && board.getPiece(x - 1, y + 1).white != white) { moves.add(new Point(x - 1, y + 1)); }
            if (board.getPiece(x + 1, y + 1) != null && board.getPiece(x + 1, y + 1).white != white) { moves.add(new Point(x + 1, y + 1)); }
        } else {
            if (board.getPiece(x, y - 1) == null) {
                moves.add(new Point(x, y - 1));
                if (y == 7 && board.getPiece(x, 5) == null) { moves.add(new Point(x, 5)); }
            }
            if (board.getPiece(x - 1, y - 1) != null && board.getPiece(x - 1, y - 1).white != white) { moves.add(new Point(x - 1, y - 1)); }
            if (board.getPiece(x + 1, y - 1) != null && board.getPiece(x + 1, y - 1).white != white) { moves.add(new Point(x + 1, y - 1)); }
        }
        break;

### Classes

|Class        |Description                                                                                                              |
|:-----------:|:-----------------------------------------------------------------------------------------------------------------------:|
|Main         |The main class. It create a new thread, creates the window and game manager class.                                       |
|WindowManager|Controls anything to do with the game window, such as graphics, window title, window size, etc.                          |
|CreateWindow |Extends JFrame. When defined will create and spawn a JFrame.                                                             |
|GameManager  |Controls the basic logic for the game such as game loops, ticks, etc.                                                    |
|Board        |Controls all the game specific logic related to the board. It creates the pieces, manages the pieces, lets you move, etc.|
|Piece        |Stores all the data related to a piece on the game board. Also used to determine what pieces are valid on a piece.       |
|Resource     |Is used to access the image files when compiled into a jar.                                                              |


## Notes

I have not coded in a way for draws with repetition, the 50 move rule, and a dead positions other then 2 kings beacuse I felt that I wanted to move onto other projects.
