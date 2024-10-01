package islands.model.player.student;

import islands.model.GameModel;
import islands.model.Move;
import islands.model.TileColor;
import islands.model.player.MinimaxPlayer;

/**
 * A player that uses caching to improve on the minimax algorithm.
 *
 * @see TranspositionTable
 */
public class CachingMinimaxPlayer extends MinimaxPlayer {
    private final TranspositionTable table;
    /**
     * Constructs a caching minimax player.
     */
    public CachingMinimaxPlayer() {
        table = new TranspositionTable();
    }

    @Override
    public String getName() {
        return "Caching Minimax";
    }

    @Override
    public Move getMyMove(GameModel model, int depth, TileColor tileColor) {
        if (table.hasMove(model, depth)) {
            return table.getMove(model, depth);
        }
        Move bestMove = super.getMyMove(model, depth, tileColor);
        table.putMove(model, depth, bestMove);
        return bestMove;
    }
}
