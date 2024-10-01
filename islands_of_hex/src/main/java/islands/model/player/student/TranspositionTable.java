package islands.model.player.student;

import islands.model.GameModel;
import islands.model.Move;

import java.util.*;

/**
 * A table for storing and retrieving the results of calls to {@link
 * islands.model.player.SimulatedGameTreePlayer#getMyMove(GameModel, int, islands.model.TileColor)}.
 */
public class TranspositionTable {
    // A record is the Java equivalent of a Kotlin data class.
    private record CachedInfo(int depth, Move move) {
    }

    private final Map<String, CachedInfo> cachedMoves = new HashMap<>();

    /**
     * Constructs an empty transposition table.
     */
    public TranspositionTable() {
    }

    /**
     * Records that calling {@link
     * islands.model.player.SimulatedGameTreePlayer#getMyMove(GameModel, int, islands.model.TileColor)}
     * with the given model and depth produced the specified move.
     *
     * @param model the model
     * @param depth the depth
     * @param move  the move
     */
    public void putMove(GameModel model, int depth, Move move) {
        String boardString = model.getBoardString();
        CachedInfo boardInfo = new CachedInfo(depth, move);
        cachedMoves.put(boardString, boardInfo);
        putTransformation(model.getSize(), boardString, boardInfo);
    }

    private String translateBoardString(int size, String boardString) {
        StringBuilder translatedBoardStringBuilder = new StringBuilder();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                translatedBoardStringBuilder.append(
                        getTileChar(size, boardString, size - 1 - row, size - 1 - col)
                );
            }
            translatedBoardStringBuilder.append("\n");
        }
        return translatedBoardStringBuilder.toString();
    }

    private Move translateMove(int size, Move move) {
        int translatedRow = size - 1 - move.row();
        int translatedCol = size - 1 - move.col();
        return new Move(translatedRow, translatedCol, move.value());
    }

    // Adds entry for the board rotated 180 degrees.
    private void putTransformation(int size, String boardString, CachedInfo cachedInfo) {
        String translatedBoardString = translateBoardString(size, boardString);
        Move translatedMove = translateMove(size, cachedInfo.move);
        cachedMoves.put(translatedBoardString, new CachedInfo(cachedInfo.depth, translatedMove));
    }

    private static char getTileChar(int size, String boardString, int row, int col) {
        int offset = row * (size + 1) + col; // + 1 for the newline character in boardString
        return boardString.charAt(offset);
    }

    /**
     * Checks whether this table has the move recommended for this model
     * (or its transformations) when searching to the specified depth or
     * deeper.
     *
     * @param model the model
     * @param depth the minimum search depth
     * @return true if a move is available, false otherwise
     */
    public boolean hasMove(GameModel model, int depth) {
        String currentBoardString = model.getBoardString();
        if (cachedMoves.containsKey(currentBoardString)) {
            CachedInfo correspondingCachedInfo = cachedMoves.get(currentBoardString);
            return correspondingCachedInfo.depth >= depth;
        }
        return false;
    }

    /**
     * Gets the stored move for this model computed to the given depth or
     * deeper.
     *
     * @param model the model
     * @param depth the depth
     * @return the stored move
     * @throws NoSuchElementException if this table does not have an entry
     *                                with the specified model with a depth
     *                                greater than or equal to the
     *                                requested depth
     */
    public Move getMove(GameModel model, int depth) {
        if (!hasMove(model, depth)) {
            throw new NoSuchElementException();
        }
        return cachedMoves.get(model.getBoardString()).move;
    }
}
