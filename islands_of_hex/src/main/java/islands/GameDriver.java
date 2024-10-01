package islands;

import islands.model.player.RandomPlayer;
import islands.model.player.SimulatedPlayer;
import islands.model.player.MinimaxPlayer;
import islands.model.player.student.AlphaBetaPlayer;
import islands.model.player.student.CachingMinimaxPlayer;
import islands.model.player.student.RandomMaxPlayer;
import islands.view.Game;

import java.awt.Dimension;
import javax.swing.*;
import java.util.*;

/**
 * Main launching point for the Hex Game.
 */
public final class GameDriver {
    // prevent instantiation
    private GameDriver() {
    }

    private static final List<Class<? extends SimulatedPlayer>> SIMULATORS = List.of(
            RandomPlayer.class,
            MinimaxPlayer.class,
            RandomMaxPlayer.class,
            CachingMinimaxPlayer.class,
            AlphaBetaPlayer.class
    );

    /**
     * Launches Islands of Hex game.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Islands of Hex");
        Game game = new Game(SIMULATORS);
        frame.add(game);
        frame.setSize(new Dimension(Game.BOARD_WIDTH, Game.BOARD_HEIGHT));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
