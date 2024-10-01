package islands.model.player.student;

import islands.model.GameModel;
import islands.model.Move;
import islands.model.RowColPair;
import islands.model.TileColor;
import islands.model.player.MinimaxPlayer;

import java.util.List;

/**
 * A player that chooses the highest-scoring move based on the assumption
 * that the opponent chooses moves randomly.
 */
public class RandomMaxPlayer extends MinimaxPlayer {
    /**
     * Constructs a player that chooses the highest-scoring move based on the assumption that the
     * opponent chooses moves randomly.
     */
    public RandomMaxPlayer() {
    }

    @Override
    public String getName() {
        return "RandomMax";
    }

    // Because this only does the opponent move, it does not need to provide a move, just a value.
    @Override
    public double getOpponentValue(GameModel model, int depth, TileColor tileColor) {
        // Handle the base cases.
        if (depth < 0) {
            throw new IllegalArgumentException();
        }
        if (depth == 0 || model.isGameOver()) {
            return getValue(model, tileColor.getOpposite());
        }
        // Compute and return the average of the child node values.
        List<RowColPair> legalPositions = getLegalPositions(model);
        double moveValueSum = 0.0;
        for (RowColPair move : legalPositions) {
            GameModel modelCopy = model.deepCopy();
            modelCopy.makePlay(move.row(), move.column(), tileColor);
            Move opponentMove = getMyMove(modelCopy, depth - 1, tileColor.getOpposite());
            moveValueSum += opponentMove.value();
        }
        return moveValueSum / legalPositions.size();
    }
}
