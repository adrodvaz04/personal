package islands.model;

import islands.model.player.student.CachingMinimaxPlayer;
import islands.model.player.MinimaxPlayer;

public class CachingMinimaxPlayerTest extends MinimaxPlayerTest {
    @Override
    public MinimaxPlayer getNewPlayer() {
        return new CachingMinimaxPlayer();
    }
}
