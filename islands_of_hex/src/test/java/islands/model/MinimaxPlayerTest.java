package islands.model;

import islands.model.player.MinimaxPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MinimaxPlayerTest {
    private static final double DELTA = .0001;
    MinimaxPlayer testPlayer;

    // This gets overridden to test subclasses of MinimaxPlayer.
    public MinimaxPlayer getNewPlayer() {
        return new MinimaxPlayer();
    }

    @BeforeEach
    public void setup() {
        testPlayer = getNewPlayer();
    }

    @Test
    public void testTree2() {
        // From an empty 2x2 board, all mini-max paths lead to 0.
        for (int depth = 1; depth < 5; depth++) {
            // Create a new player every time to clear the transposition table.
            MinimaxPlayer player = getNewPlayer();
            GameModel model = new GameModelImplementation(2);
            Move move = player.getMyMove(model, depth, TileColor.WHITE);
            assertNotNull(move);
            assertNotNull(move.getPosition());
            if (depth == 1) {
                // If there's been only one move, White will be ahead 1-0.
                assertEquals(1, move.value(), DELTA);
            } else {
                assertEquals(0, move.value(), DELTA);
            }
            // Since moves have same expected outcome, the first is chosen.
            assertEquals(0, move.getPosition().row());
            assertEquals(0, move.getPosition().column());
        }
    }

    private void makeDepth2WhiteMove(GameModel model, int expectedRow, int expectedCol, double expectedValue) {
        Move move = testPlayer.getMyMove(model, 2, TileColor.WHITE);
        assertNotNull(move);
        assertEquals(expectedValue, move.value(), DELTA);
        assertEquals(expectedRow, move.row());
        assertEquals(expectedCol, move.col());
        model.makePlay(expectedRow, expectedCol, TileColor.WHITE);
    }

    @Test
    public void testTree3Depth2() {
        GameModel model = new GameModelImplementation(3);
        makeDepth2WhiteMove(model, 0, 0, 0.0);
        model.makePlay(2, 2, TileColor.BLACK);
        makeDepth2WhiteMove(model, 0, 2, 0.0);
        model.makePlay(0, 1, TileColor.BLACK);
        makeDepth2WhiteMove(model, 2, 0, 0.0);
        model.makePlay(1, 0, TileColor.BLACK);
        makeDepth2WhiteMove(model, 1, 2, 9.0);
    }

    private void testPlayersProvideSameMove(MinimaxPlayer player1, MinimaxPlayer player2, GameModel model, int depth, TileColor tileColor) {
        Move move1 = player1.getMyMove(model, depth, tileColor);
        Move move2 = player2.getMyMove(model, depth, tileColor);
        assertEquals(move1, move2, String.format("model: %s, player: %s", model.toString(), player2.getName()));
    }

    // Make sure that MinimaxPlayer subclasses behave the same as Minimax.
    @Test
    public void testBehaviorSameAsMinimax() {
        if (testPlayer.getClass().equals(MinimaxPlayer.class)) {
            // No point checking MinimaxPlayer against itself.
            return;
        }

        // If we get here, testPlayer is an instance of a subclass of
        // MinimaxPlayer.
        int depth = 2; // keep it fast
        List<String> testRows = List.of(
                "nnnnn",
                "WnnnB",
                "WBnnn",
                "WWBBn",
                "WnWBB",
                "BWnWB"
        );
        String emptyRow = "nnnnn";
        MinimaxPlayer minimaxPlayer = new MinimaxPlayer();
        for (String testRow : testRows) {
            for (TileColor tileColor : TileColor.values()) {
                if (tileColor == TileColor.NONE) {
                    continue;
                }

                // Have testRow on top only.
                testPlayer = getNewPlayer();
                GameModel model1 = TestHelperMethods.makeModel(testRow, emptyRow, emptyRow, emptyRow, emptyRow);
                testPlayersProvideSameMove(minimaxPlayer, testPlayer, model1, depth, tileColor);

                // Have testRow on top and bottom.
                testPlayer = getNewPlayer();
                GameModel model2 = TestHelperMethods.makeModel(testRow, emptyRow, emptyRow, emptyRow, testRow);
                testPlayersProvideSameMove(minimaxPlayer, testPlayer, model2, depth, tileColor);
            }
        }
    }
}
