package islands.model.player;

import islands.model.*;

/**
 * A player that chooses the highest-scoring move based on the assumption
 * that the opponent will always choose the lowest-scoring move.
 */
public class MinimaxPlayer extends SimulatedGameTreePlayer {
    /**
     * The maximum depth of the search tree from the current position.
     */
    protected static final int MAX_DEPTH = 5;

    /**
     * Constructs a minimax player.
     */
    public MinimaxPlayer() {
    }

    @Override
    public String getName() {
        return "Minimax";
    }

    @Override
    public RowColPair chooseNextMove(GameModel model, TileColor tileColor) {
        Move move = getMyMove(model, MAX_DEPTH, tileColor);
        return move.getPosition();
    }

    @Override
    public Move getMyMove(GameModel model, int depth, TileColor tileColor) {
        if (depth < 0) {
            throw new IllegalArgumentException();
        }
        if (depth == 0 || model.isGameOver()) {
            // In this case, we should not investigate any moves, so we just
            // construct and return an instance of Move that has the estimated
            // value but no position.
            return new Move(getValue(model, tileColor));
        }

        // Try every legal move, saving whichever one gives us the best outcome.
        Move bestMove = null;
        for (RowColPair position : getLegalPositions(model)) {
            GameModel newModel = model.deepCopy();
            newModel.makePlay(position.row(), position.column(), tileColor);
            double childValue = getOpponentValue(newModel, depth - 1, tileColor.getOpposite());
            if (bestMove == null || childValue > bestMove.value()) {
                bestMove = new Move(position.row(), position.column(), childValue);
            }
        }
        return bestMove;
    }

    @Override
    public double getOpponentValue(GameModel model, int depth, TileColor tileColor) {
        if (depth < 0) {
            throw new IllegalArgumentException();
        }

        // In this case, we should not investigate any moves, so we just
        // construct and return an instance of Move that has the estimated
        // value but no position.
        if (depth == 0 || model.isGameOver()) {
            return getValue(model, tileColor.getOpposite());
        }

        // Try every legal move, saving whichever one gives our opponent the worst outcome.
        double minValue = Integer.MAX_VALUE;
        for (RowColPair position : getLegalPositions(model)) {
            GameModel childModel = model.deepCopy();
            childModel.makePlay(position.row(), position.column(), tileColor);
            Move childMove = getMyMove(childModel, depth - 1, tileColor.getOpposite());
            if (childMove.value() < minValue) {
                minValue = childMove.value();
            }
        }
        return minValue;
    }
}
