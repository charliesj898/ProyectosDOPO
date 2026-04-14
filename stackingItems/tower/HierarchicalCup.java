package tower;

import shapes.Circle;

/**
 * Taza jerarquica. Si toca el suelo de la torre (Y=0), no puede ser eliminada.
 * Se distingue visualmente por un circulo blanco en su pared izquierda.
 *
 * @author Carlos Sanchez, Samuel Argalle
 * @version 3.0
 */
public class HierarchicalCup extends Cup {

    private Circle badge;

    public HierarchicalCup(int number, int size) {
        super(number, size);
        badge = new Circle();
        badge.changeColor("white"); // Color no usado en paleta
        badge.changeSize(size + 2); // Medalla jerárquica incrustada en la pared
    }

    @Override
    public StackableItem createCopy() {
        return new HierarchicalCup(this.number, this.size);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        // Centrar la medalla dentro del cuerpo: encima de la pared izquierda
        int centerY = y + ((this.getHeight() * this.size) / 2);
        badge.setPosition(x, centerY - (size + 2) / 2);
    }

    @Override
    public void makeVisible() {
        super.makeVisible();
        if (isVisible) badge.makeVisible();
    }

    @Override
    public void makeInvisible() {
        super.makeInvisible();
        badge.makeInvisible();
    }

    @Override
    public boolean canBeRemoved(int contactY) {
        return contactY > 0;
    }
}
