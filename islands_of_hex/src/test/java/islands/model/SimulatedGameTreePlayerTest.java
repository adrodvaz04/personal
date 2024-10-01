package islands.model;

import islands.model.player.SimulatedGameTreePlayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimulatedGameTreePlayerTest {
    private static final double DELTA = .001;

    // Create stub methods so we can instantiate the class.
    // We will only call getValue().
    SimulatedGameTreePlayer player = new SimulatedGameTreePlayer() {
        @Override
        public RowColPair chooseNextMove(GameModel model, TileColor tileColor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Move getMyMove(GameModel model, int depth, TileColor tileColor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getOpponentValue(GameModel model, int depth, TileColor tileColor) {
            throw new UnsupportedOperationException();
        }
    };

    private void testValue(double expectedWhiteValue, String... pattern) {
        GameModel model = new GameModelImplementation(pattern.length);
        TestHelperMethods.fill(model, pattern.length, pattern);
        double whiteValue = player.getValue(model, TileColor.WHITE);
        double blackValue = player.getValue(model, TileColor.BLACK);
        assertEquals(whiteValue, -blackValue, DELTA);
        assertEquals(expectedWhiteValue, whiteValue);
    }

    @Test
    public void getValueOnSize2Board() {
        testValue(0, "nn", "nn");
        testValue(1, "Wn", "nn");
        testValue(0, "WB", "nn");
        testValue(0, "WB", "Wn"); // game over
        testValue(0, "WB", "WB");
        testValue(-4, "WB", "BW"); // game over
    }
}
