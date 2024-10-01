package islands.controller;

import islands.model.*;
import islands.model.GameModelImplementation;
import islands.model.player.SimulatedPlayer;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Controller for the game.
 */
public class GameController implements MouseListener {
    private static final TileColor START_COLOR = TileColor.WHITE;
    private static final int MS_BETWEEN_SIMULATED_MOVES = 10;

    private final GameModel model;
    private Timer timer;
    private ViewDelegate viewDelegate;
    private TileColor turn = START_COLOR;
    private boolean gameOver = false;

    private SimulatedPlayer whiteSimulatedPlayer;
    private SimulatedPlayer blackSimulatedPlayer;

    /**
     * The interface for views of the game.
     */
    public interface ViewDelegate {
        /**
         * Displays the turn for the given color.
         *
         * @param color the player whose turn it is
         */
        void displayTurn(TileColor color);

        /**
         * Adds a piece with the specified location and color.
         *
         * @param row       the destination row
         * @param col       the destination column
         * @param tileColor the color of the piece
         */
        void setColor(int row, int col, TileColor tileColor);

        /**
         * Sets the mouse listener.
         *
         * @param ml the mouse listener
         */
        void listen(MouseListener ml);

        /**
         * Converts screen coordinates into a board position.
         *
         * @param y y-coordinate
         * @param x x-coordinate
         * @return the board position
         */
        Optional<RowColPair> getHexDim(int y, int x);

        /**
         * Displays the winner of the game.
         *
         * @param winner the winner
         */
        void setWinner(TileColor winner);

        /**
         * Displays the score.
         *
         * @param whiteScore white's score
         * @param blackScore black's score
         */
        void setScore(int whiteScore, int blackScore);
    }

    /**
     * Creates a game controller.
     *
     * @param size       the size of the board
     * @param whiteClass the white player
     * @param blackClass the black player
     * @param vd         the view delegate
     */
    public GameController(
            int size,
            Class<? extends SimulatedPlayer> whiteClass,
            Class<? extends SimulatedPlayer> blackClass,
            ViewDelegate vd) {
        model = new GameModelImplementation(size);

        // Create simulators if requested.
        if (whiteClass != null) {
            this.whiteSimulatedPlayer = constructSimulatedPlayer(whiteClass);
        }
        if (blackClass != null) {
            this.blackSimulatedPlayer = constructSimulatedPlayer(blackClass);
        }
        if (whiteClass != null || blackClass != null && MS_BETWEEN_SIMULATED_MOVES > 0) {
            timer = new Timer(MS_BETWEEN_SIMULATED_MOVES, (e) -> runSimulatedPlayers());
            timer.start();
        }

        setDelegate(vd);
    }

    /**
     * Constructs a simulated player.
     *
     * @param clazz the class of the simulated player, which must have a no-args constructor
     * @return an instance of the class
     */
    public static SimulatedPlayer constructSimulatedPlayer(
            Class<? extends SimulatedPlayer> clazz
    ) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException
                 | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(String.format("Error trying to call constructor %s()", clazz.getSimpleName()));
        }
    }

    /**
     * Changes the turn from white to black or vice versa.
     */
    public void toggleTurn() {
        turn = turn.getOpposite();
        viewDelegate.displayTurn(turn);
    }


    private void runSimulatedPlayers() {
        if (turn == TileColor.WHITE && whiteSimulatedPlayer != null) {
            tryRowColFromPoint(whiteSimulatedPlayer.timeAndChooseNextMove(model, turn));
        } else if (turn == TileColor.BLACK && blackSimulatedPlayer != null) {
            tryRowColFromPoint(blackSimulatedPlayer.timeAndChooseNextMove(model, turn));
        }
    }

    /**
     * Sets the view delegate and starts the game.
     *
     * @param vd the view delegate
     */
    public void setDelegate(ViewDelegate vd) {
        viewDelegate = vd;
        vd.displayTurn(START_COLOR);
        vd.listen(this);
    }

    /**
     * Tries to play a game piece at the specified position. If the piece can be played,
     * this updates the model and view, including detecting whether the game has ended and,
     * if not, toggling whose turn it is. If the piece cannot be played, this call has no effect.
     *
     * @param position the position
     */
    public void tryRowColFromPoint(RowColPair position) {
        tryRowCol(position.row(), position.column());
    }

    /**
     * Tries to play a game piece at the specified position. If the piece can be played,
     * this updates the model and view, including detecting whether the game has ended and,
     * if not, toggling whose turn it is. If the piece cannot be played, this call has no effect.
     *
     * @param row the row
     * @param col the column
     */
    public void tryRowCol(int row, int col) {
        if (model.canPlay(row, col)) {
            // I'm sometimes getting an NPE after trying to start a new game
            viewDelegate.setColor(row, col, turn);
            model.makePlay(row, col, turn);
            if (model.isGameOver()) {
                stopAll();
                gameOver = true;
                viewDelegate.setWinner(getWinner());
                if (whiteSimulatedPlayer != null) {
                    System.out.printf(whiteSimulatedPlayer.getTimeSummary(TileColor.WHITE));
                }
                if (blackSimulatedPlayer != null) {
                    System.out.printf(blackSimulatedPlayer.getTimeSummary(TileColor.BLACK));
                }
            } else {
                toggleTurn();
            }
            viewDelegate.setScore(model.getScore(TileColor.WHITE), model.getScore(TileColor.BLACK));
        }
    }

    // The caller is responsible for ensuring that the game has ended.
    private TileColor getWinner() {
        int whiteScore = model.getScore(TileColor.WHITE);
        int blackScore = model.getScore(TileColor.BLACK);
        return switch (Integer.signum(whiteScore - blackScore)) {
            case -1 -> TileColor.BLACK;
            case 1 -> TileColor.WHITE;
            default -> TileColor.NONE;  // tie
        };
    }

    /**
     * Stops the timer.
     */
    public void stopAll() {
        if (timer != null) {
            timer.stop();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Ignore mouse presses when a simulator is running.
        if (turn == TileColor.WHITE && whiteSimulatedPlayer != null
                || turn == TileColor.BLACK && blackSimulatedPlayer != null) {
            return;
        }
        // Do nothing if the game is over.
        if (!gameOver) {
            Optional<RowColPair> point = viewDelegate.getHexDim(e.getY(), e.getX());
            point.ifPresent(this::tryRowColFromPoint);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // ignore mouse clicks
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // ignore mouse releases
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // ignores when the mouse enters a component
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // ignores when the mouse exits a component
    }
}
