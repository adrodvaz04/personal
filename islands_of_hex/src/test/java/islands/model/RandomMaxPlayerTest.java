package islands.model;

import islands.model.player.student.RandomMaxPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RandomMaxPlayerTest {
    private static final double DELTA = .001;
    RandomMaxPlayer player;

    @BeforeEach
    public void setup() {
        player = new RandomMaxPlayer();
    }

    @Test
    public void getMakeMoveSize2Empty() {
        GameModel model = new GameModelImplementation(2);
        Move move = player.getMyMove(model, 4, TileColor.WHITE);
        double expectedValue = 8.0 / 3;
        assertEquals(expectedValue, move.value(), DELTA);
    }
}
