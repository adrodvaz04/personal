package islands.view;

import islands.model.RowColPair;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Optional;

/**
 * The view component displaying the hexagonal grid.
 */
public class HexGrid extends JComponent {
    private static final int PREFERRED_WIDTH = 600;
    private static final int PREFERRED_HEIGHT = 800;
    private static final double HEIGHT_OFFSET_SCALAR = .75;
    private final transient int size; // number of tiles in each row/column
    private final transient Hexagon[][] hexagons;

    /**
     * Create a Hex Grid with the given size (width and height).
     *
     * @param size the size
     */
    public HexGrid(int size) {
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        this.size = size;
        hexagons = new Hexagon[size][size];
        makeHexagons();
    }

    // maps from grid dimension to hexagon length, so game board fits in the frame.
    private static final Map<Integer, Integer> HEX_LENGTHS = Map.of(
            // If the grid dimension is greater than 20, hexagon length should be 20.
            20, 10,
            10, 20,
            6, 25,
            0, 35
    );

    private static int determineHexagonLength(int size) {
        while (!HEX_LENGTHS.containsKey(size)) {
            size--;
        }
        return HEX_LENGTHS.get(size);
    }

    /**
     * Creates the hexagonal tiles.
     */
    private void makeHexagons() {
        int hexLength = determineHexagonLength(size);
        int topLeftY = (int) (Game.BOARD_HEIGHT / 2.0 - size * HEIGHT_OFFSET_SCALAR * hexLength);
        int topLeftX = (int) (Game.BOARD_WIDTH / 2.0 - size * hexLength);

        for (int col = 0; col < size; col++) {
            int x = topLeftX + col * (hexLength - 1) * 2;
            int y = topLeftY - col * (hexLength - 2);
            for (int row = 0; row < size; row++) {
                int columnY = y + row * (hexLength) * 2;
                hexagons[row][col] = new Hexagon(x, columnY, Color.LIGHT_GRAY, hexLength);
            }
        }
    }

    /**
     * Sets the color of the hexagon at a given position.
     *
     * @param row   the row
     * @param col   the column
     * @param color the color
     */
    public void setColor(int row, int col, Color color) {
        hexagons[row][col].setColor(color);
        repaint();
    }

    /**
     * Finds the board position (row and column) corresponding to the pixel location.
     *
     * @param y the y-coordinate
     * @param x the x-coordinate
     * @return the corresponding board location
     */
    public Optional<RowColPair> getHexDim(int y, int x) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (hexagons[row][col].getPolygon().contains(x, y)) {
                    return Optional.of(new RowColPair(row, col));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void paintComponent(Graphics g) {
        for (Hexagon[] hs : hexagons) {
            for (Hexagon h : hs) {
                h.draw(g);
            }
        }
    }
}
