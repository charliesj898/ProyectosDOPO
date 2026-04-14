package tower;

import shapes.Triangle;

/**
 * Representa una tapa malvada (Evil Lid) en el simulador.
 * Hereda de Lid. Cuando logra cubrir a su taza exacta,
 * actua como una trampa: se autodestruye junto con su taza,
 * eliminando ambos elementos de la torre permanentemente.
 * 
 * Visualmente se distingue por un triangulo negro centrado sobre ella.
 * 
 * @author Carlos Sanchez, Samuel Argalle
 * @version 3.0
 */
public class EvilLid extends Lid {

    private Triangle skull;

    /**
     * Crea una EvilLid con el numero y tamano dados.
     * 
     * @param number el numero de la tapa (y de su taza victima)
     * @param size   pixeles por cm para la representacion
     */
    public EvilLid(int number, int size) {
        super(number, size);
        skull = new Triangle();
        skull.changeColor("black"); // Negro no esta en la paleta COLORS[]
        skull.changeSize(size, size);
    }

    @Override
    public StackableItem createCopy() {
        return new EvilLid(this.number, this.size);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        // Centrar el simbolo malvado dentro del rectangulo de la tapa
        int centerX = x + (this.number * this.size) - size / 2;
        skull.setPosition(centerX, y);
    }

    @Override
    public void makeVisible() {
        super.makeVisible();
        if (isVisible)
            skull.makeVisible();
    }

    @Override
    public void makeInvisible() {
        super.makeInvisible();
        skull.makeInvisible();
    }

    /**
     * Si esta tapa acaba de cubrir a su taza exacta, se autodestruye
     * junto con dicha taza, eliminando ambos de la torre.
     */
    @Override
    public void onAfterPlaced(java.util.ArrayList<StackableItem> items) {
        // Buscar si existe una taza con el mismo numero en la lista
        Cup targetCup = null;
        for (StackableItem item : items) {
            if (item.isCup() && item.getNumber() == this.number) {
                targetCup = (Cup) item;
                break;
            }
        }

        // Si esta cubriendo a su taza exacta, actua la trampa
        if (targetCup != null && targetCup.getLid() == this) {
            targetCup.removeLid();
            targetCup.makeInvisible();
            this.makeInvisible();
            items.remove(targetCup);
            items.remove(this);
        }
    }
}
