package shapes;
/**
 * A generic Shape that can be manipulated and that draws itself on a canvas.
 */
public abstract class Shape {

    protected int xPosition;
    protected int yPosition;
    protected String color;
    protected boolean isVisible;

    /**
     * Make this shape visible. If it was already visible, do nothing.
     */
    public void makeVisible() {
        isVisible = true;
        draw();
    }

    /**
     * Make this shape invisible. If it was already invisible, do nothing.
     */
    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    /**
     * Move the shape a few pixels to the right.
     */
    public void moveRight() {
        moveHorizontal(20);
    }

    /**
     * Move the shape a few pixels to the left.
     */
    public void moveLeft() {
        moveHorizontal(-20);
    }

    /**
     * Move the shape a few pixels up.
     */
    public void moveUp() {
        moveVertical(-20);
    }

    /**
     * Move the shape a few pixels down.
     */
    public void moveDown() {
        moveVertical(20);
    }

    /**
     * Move the shape horizontally.
     * @param distance the desired distance in pixels
     */
    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Move the shape vertically.
     * @param distance the desired distance in pixels
     */
    public void moveVertical(int distance) {
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Set the absolute position of the shape.
     * 
     * @param x the new x position
     * @param y the new y position
     */
    public void setPosition(int x, int y) {
        erase();
        xPosition = x;
        yPosition = y;
        draw();
    }

    /**
     * Slowly move the shape horizontally.
     * @param distance the desired distance in pixels
     */
    public void slowMoveHorizontal(int distance) {
        int delta;

        if (distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }

        for (int i = 0; i < distance; i++) {
            xPosition += delta;
            draw();
        }
    }

    /**
     * Slowly move the shape vertically.
     * @param distance the desired distance in pixels
     */
    public void slowMoveVertical(int distance) {
        int delta;

        if (distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }

        for (int i = 0; i < distance; i++) {
            yPosition += delta;
            draw();
        }
    }

    /**
     * Change the color.
     * 
     * @param color the new color.
     */
    public void changeColor(String newColor) {
        color = newColor;
        draw();
    }

    /**
     * Draw the shape with current specifications on screen.
     */
    protected abstract void draw();

    /**
     * Erase the shape on screen.
     */
    protected void erase() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }
}
