package tower;

import shapes.Circle;

/**
 * Tapa miedosa. Solo entra si su taza ya esta en la torre.
 * Si esta cubriendo a su taza, no puede ser eliminada.
 * Se distingue visualmente por un circulo blanco centrado en la tapa.
 *
 * @author Carlos Sanchez, Samuel Argalle
 * @version 3.0
 */
public class FearfulLid extends Lid {

    private Circle tear;

    public FearfulLid(int number, int size) {
        super(number, size);
        tear = new Circle();
        tear.changeColor("white"); // Color no usado en paleta
        tear.changeSize(Math.max(4, size - 2)); 
    }

    @Override
    public StackableItem createCopy() {
        return new FearfulLid(this.number, this.size);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        int tearSize = Math.max(4, this.size - 2);
        // Centro horizontal de la tapa + centrado vertical dentro del 1cm de altura
        int centerX = x + (this.number * this.size) - tearSize / 2;
        int centerY = y + (this.size - tearSize) / 2;
        tear.setPosition(centerX, centerY);
    }

    @Override
    public void makeVisible() {
        super.makeVisible();
        if (isVisible) tear.makeVisible();
    }

    @Override
    public void makeInvisible() {
        super.makeInvisible();
        tear.makeInvisible();
    }

    @Override
    public boolean canBeRemoved(boolean isCoveringExactMaster) {
        return !isCoveringExactMaster; // No sale si está cubierta por su maestro
    }

    @Override
    public boolean canEnterTower(boolean hasMasterCup) {
        return hasMasterCup; // Solo entra si el maestro está
    }
}
