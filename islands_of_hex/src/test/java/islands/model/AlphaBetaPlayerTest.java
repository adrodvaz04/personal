package islands.model;

import islands.model.player.student.AlphaBetaPlayer;
import islands.model.player.MinimaxPlayer;

public class AlphaBetaPlayerTest extends MinimaxPlayerTest {
    @Override
    public MinimaxPlayer getNewPlayer() {
        return new AlphaBetaPlayer();
    }
}
