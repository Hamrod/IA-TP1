package fr.istic.ia.tp1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import fr.istic.ia.tp1.Game.PlayerId;

/**
 * Implementation of the English Draughts game.
 *
 * @author vdrevell
 */
public class EnglishDraughts extends Game {
    /**
     * The checker board
     */
    CheckerBoard board;

    /**
     * The {@link PlayerId} of the current player
     * {@link PlayerId#ONE} corresponds to the whites
     * {@link PlayerId#TWO} corresponds to the blacks
     */
    PlayerId playerId;

    /**
     * The current game turn (incremented each time the whites play)
     */
    int nbTurn;

    /**
     * The number of consecutive moves played only with kings and without capture
     * (used to decide equality)
     */
    int nbKingMovesWithoutCapture;


    /**
     * Class representing a move in the English draughts game
     * A move is an ArrayList of Integers, corresponding to the successive tile numbers (Manouri notation)
     * toString is overrided to provide Manouri notation output.
     *
     * @author vdrevell
     */
    class DraughtsMove extends ArrayList<Integer> implements Game.Move {

        private static final long serialVersionUID = -8215846964873293714L;

        @Override
        public String toString() {
            Iterator<Integer> it = this.iterator();
            Integer from = it.next();
            StringBuffer sb = new StringBuffer();
            sb.append(from);
            while (it.hasNext()) {
                Integer to = it.next();
                if (board.neighborDownLeft(from) == to || board.neighborUpLeft(from) == to
                        || board.neighborDownRight(from) == to || board.neighborUpRight(from) == to) {
                    sb.append('-');
                } else {
                    sb.append('x');
                }
                sb.append(to);
                from = to;
            }
            return sb.toString();
        }
    }

    /**
     * The default constructor: initializes a game on the standard 8x8 board.
     */
    public EnglishDraughts() {
        this(8);
    }

    /**
     * Constructor with custom boardSize (to play on a boardSize x boardSize checkerBoard).
     *
     * @param boardSize See {@link CheckerBoard#CheckerBoard(int)} for valid board sizes.
     */
    public EnglishDraughts(int boardSize) {
        this.board = new CheckerBoard(boardSize);
        this.playerId = PlayerId.ONE;
        this.nbTurn = 1;
        this.nbKingMovesWithoutCapture = 0;
    }

    /**
     * Copy constructor
     *
     * @param d The game to copy
     */
    EnglishDraughts(EnglishDraughts d) {
        this.board = d.board.clone();
        this.playerId = d.playerId;
        this.nbTurn = d.nbTurn;
        this.nbKingMovesWithoutCapture = d.nbKingMovesWithoutCapture;
    }

    @Override
    public EnglishDraughts clone() {
        return new EnglishDraughts(this);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(nbTurn);
        sb.append(". ");
        sb.append(this.playerId == PlayerId.ONE ? "W" : "B");
        sb.append(":");
        sb.append(board.toString());
        return sb.toString();
    }

    @Override
    public String playerName(PlayerId playerId) {
        switch (playerId) {
            case ONE:
                return "Player with the whites";
            case TWO:
                return "Player with the blacks";
            case NONE:
            default:
                return "Nobody";
        }
    }

    @Override
    public String view() {
        return board.boardView() + "Turn #" + nbTurn + ". " + playerName(playerId) + " plays.\n";
    }

    /**
     * Check if a tile is empty
     *
     * @param square Tile number
     * @return
     */
    boolean isEmpty(int square) {
        return board.isEmpty(square);
    }

    /**
     * Check if a tile is owned by adversary
     *
     * @param square Tile number
     * @return
     */
    boolean isAdversary(int square) {
        if (playerId == playerId.TWO && board.isWhite(square)) {
            return true;
        } else if (playerId == playerId.ONE && board.isBlack(square)) {
            return true;
        }
        return false;
    }

    /**
     * Check if a tile is owned by the current player
     *
     * @param square Tile number
     * @return
     */
    boolean isMine(int square) {
        if (playerId == playerId.ONE && board.isWhite(square)) {
            return true;
        } else if (playerId == playerId.TWO && board.isBlack(square)) {
            return true;
        }
        return false;
    }

    /**
     * Retrieve the list of positions of the pawns owned by the current player
     *
     * @return The list of current player pawn positions
     */
    ArrayList<Integer> myPawns() {
        switch (playerId) {
            case ONE -> {
                return board.getWhitePawns();
            }
            case TWO -> {
                return board.getBlackPawns();
            }
            case NONE -> {
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }


    /**
     * Generate the list of possible moves
     * - first check moves with captures
     * - if no capture possible, return displacement moves
     */
    @Override
    public List<Move> possibleMoves() {
        // Advice:
        // create two auxiliary functions :
        // - one for jump moves from a given position, with capture (and multi-capture).
        //    Use recursive calls to explore all multiple capture possibilities
        // - one function that returns the displacement moves from a given position (without capture)
        //
        ArrayList<Move> moves = new ArrayList<>();

        for (int p :
                myPawns()) {
            DraughtsMove move = new DraughtsMove();
            move.add(p);
            moves.addAll(possibleCaptureMovements(move));
        }
        if (moves.isEmpty()) {
            for (int p :
                    myPawns()) {
                moves.addAll(noCaptureMovements(p));
            }
        }

        System.out.println(moves);
        return moves;
    }

    private ArrayList<Move> noCaptureMovements(int from) {
        ArrayList<Move> moves = new ArrayList<>();

        if (board.isWhite(from) || board.isKing(from)) {

            int upLeft = board.neighborUpLeft(from);
            int upRight = board.neighborUpRight(from);

            if (upLeft != 0) {
                if (isEmpty(upLeft)) {
                    DraughtsMove upLeftMove = new DraughtsMove();
                    upLeftMove.add(from);
                    upLeftMove.add(upLeft);
                    moves.add(upLeftMove);
                }
            }
            if (upRight != 0) {
                if (isEmpty(upRight)) {
                    DraughtsMove upRightMove = new DraughtsMove();
                    upRightMove.add(from);
                    upRightMove.add(upRight);
                    moves.add(upRightMove);
                }
            }

        }
        if (board.isBlack(from) || board.isKing(from)) {

            int downLeft = board.neighborDownLeft(from);
            int downRight = board.neighborDownRight(from);

            if (downLeft != 0) {
                if (isEmpty(downLeft)) {
                    DraughtsMove downLeftMove = new DraughtsMove();
                    downLeftMove.add(from);
                    downLeftMove.add(downLeft);
                    moves.add(downLeftMove);
                }
            }
            if (downRight != 0) {
                if (isEmpty(downRight)) {
                    DraughtsMove downRightMove = new DraughtsMove();
                    downRightMove.add(from);
                    downRightMove.add(downRight);
                    moves.add(downRightMove);
                }
            }

        }

        return moves;
    }

    private ArrayList<Move> possibleCaptureMovements(DraughtsMove move) {

        ArrayList<Move> moves = new ArrayList<>();
        int from = move.get(move.size() - 1);
        int pawn = move.get(0);

        if (board.isWhite(pawn) || board.isKing(pawn)) {

            int upLeft = board.neighborUpLeft(from);
            int upUpLeft = board.neighborUpLeft(upLeft);
            int upRight = board.neighborUpRight(from);
            int upUpRight = board.neighborUpRight(upRight);

            if (upLeft != 0 && upUpLeft != 0) {
                if (isAdversary(upLeft) && isEmpty(upUpLeft)) {
                    DraughtsMove upLeftMove = new DraughtsMove();
                    upLeftMove.addAll(move);
                    upLeftMove.add(upUpLeft);
                    ArrayList<Move> nextMoves = new ArrayList<>();
                    nextMoves.addAll(possibleCaptureMovements(upLeftMove));
                    if (nextMoves.isEmpty()) {
                        moves.add(upLeftMove);
                    } else {
                        moves.addAll(nextMoves);
                    }
                }
            }
            if (upRight != 0 && upUpRight != 0) {
                if (isAdversary(upRight) && isEmpty(upUpRight)) {
                    DraughtsMove upRightMove = new DraughtsMove();
                    upRightMove.addAll(move);
                    upRightMove.add(upUpRight);
                    ArrayList<Move> nextMoves = new ArrayList<>();
                    nextMoves.addAll(possibleCaptureMovements(upRightMove));
                    if (nextMoves.isEmpty()) {
                        moves.add(upRightMove);
                    } else {
                        moves.addAll(nextMoves);
                    }
                }
            }

        }
        if (board.isBlack(pawn) || board.isKing(pawn)) {

            int downLeft = board.neighborDownLeft(from);
            int downDownLeft = board.neighborDownLeft(downLeft);
            int downRight = board.neighborDownRight(from);
            int downDownRight = board.neighborDownRight(downRight);

            if (downLeft != 0 && downDownLeft != 0) {
                if (isAdversary(downLeft) && isEmpty(downDownLeft)) {
                    DraughtsMove downLeftMove = new DraughtsMove();
                    downLeftMove.addAll(move);
                    downLeftMove.add(downDownLeft);
                    ArrayList<Move> nextMoves = new ArrayList<>();
                    nextMoves.addAll(possibleCaptureMovements(downLeftMove));
                    if (nextMoves.isEmpty()) {
                        moves.add(downLeftMove);
                    } else {
                        moves.addAll(nextMoves);
                    }
                }
            }
            if (downRight != 0 && downDownRight != 0) {
                if (isAdversary(downRight) && isEmpty(downDownRight)) {
                    DraughtsMove downRightMove = new DraughtsMove();
                    downRightMove.addAll(move);
                    downRightMove.add(downDownRight);
                    ArrayList<Move> nextMoves = new ArrayList<>();
                    nextMoves.addAll(possibleCaptureMovements(downRightMove));
                    if (nextMoves.isEmpty()) {
                        moves.add(downRightMove);
                    } else {
                        moves.addAll(nextMoves);
                    }
                }
            }
        }

        return moves;
    }


    @Override
    public void play(Move aMove) {
        // Player should be valid
        if (playerId == PlayerId.NONE)
            return;
        // We will cast Move to DraughtsMove (kind of ArrayList<Integer>
        if (!(aMove instanceof DraughtsMove))
            return;
        // Cast and apply the move
        DraughtsMove move = (DraughtsMove) aMove;

        // Move pawn and capture opponents
        //FIXME Ne fonctionne probablement pas
        Iterator<Integer> it = move.iterator();
        Integer from = it.next();
        StringBuffer sb = new StringBuffer();
        sb.append(from);
        while (it.hasNext()) {
            Integer to = it.next();
            board.movePawn(from, to);
            int pawnTaken = board.squareBetween(from, to);
            if (pawnTaken != 0) {
                board.removePawn(pawnTaken);
                nbKingMovesWithoutCapture = -1;
            }

            from = to;
        }


        // Promote to king if the pawn ends on the opposite of the board
        int squarePawn = move.get(move.size() - 1);

        if (this.playerId == playerId.ONE) {
            if (board.inTopRow(squarePawn) && board.get(squarePawn) == board.WHITE_CHECKER) {
                board.crownPawn(move.get(move.size() - 1));
            }
        } else {
            if (board.inBottomRow(squarePawn) && board.get(squarePawn) == board.BLACK_CHECKER) {
                board.crownPawn(move.get(move.size() - 1));
                board.crownPawn(move.get(move.size() - 1));
            }
        }

        // Next player
        if (player() == playerId.ONE) {
            this.playerId = playerId.TWO;
        } else {
            this.playerId = playerId.ONE;
        }
        // Update nbTurn
        nbTurn++;
        // Keep track of successive moves with kings without capture
        nbKingMovesWithoutCapture++;
    }


    @Override
    public PlayerId player() {
        return playerId;
    }

    /**
     * Get the winner (or null if the game is still going)
     * Victory conditions are :
     * - adversary with no more pawns or no move possibilities
     * Null game condition (return PlayerId.NONE) is
     * - more than 25 successive moves of only kings and without any capture
     */
    @Override
    public PlayerId winner() {
        // return the winner ID if possible
        if (board.getWhitePawns().isEmpty()) return PlayerId.TWO;
        else if (board.getBlackPawns().isEmpty()) return PlayerId.ONE;
            // return PlayerId.NONE if the game is null
        else if (nbKingMovesWithoutCapture >= 25) return PlayerId.NONE;
            // Return null is the game has not ended yet
        else return null;
    }
}
