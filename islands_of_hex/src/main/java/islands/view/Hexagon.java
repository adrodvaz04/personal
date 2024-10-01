package islands.view;

import java.awt.*;

/**
 * A single hexagonal tile on the game board.
 */
public class Hexagon {
    private static final int NUM_SIDES = 6;
    private static final double THETA = (Math.PI * 2) / NUM_SIDES;
    private final Polygon poly;
    private Color color;

    /**
     * Creates a hexagonal tile at a given column and row.
     *
     * @param x      the column
     * @param y      the row
     * @param color  the color
     * @param length the length of the tile in pixels
     */
    public Hexagon(int x, int y, Color color, int length) {
        this.color = color;
        poly = new Polygon();
        for (int i = 0; i < NUM_SIDES; i++) {
            int x1 = (int) (x + length * Math.cos(THETA * i));
            int y1 = (int) (y + length * Math.sin(THETA * i));
            poly.addPoint(x1, y1);
        }
    }

    /**
     * Sets the color of the tile.
     *
     * @param color the color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the polygon of this hexagon.
     *
     * @return the polygon.
     */
    public Polygon getPolygon() {
        return poly;
    }

    /**
     * Draws the hexagon.
     *
     * @param graphics the graphics object for rendering the hexagon
     */
    public void draw(Graphics graphics) {
        graphics.setColor(color);
        graphics.fillPolygon(poly);
    }
}
