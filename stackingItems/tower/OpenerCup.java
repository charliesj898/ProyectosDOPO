package tower;

import shapes.Triangle;

/**
 * Representa una taza rompedora (Opener Cup) en el simulador.
 * Hereda de Cup pero cuenta con la propiedad especial de romper
 * cualquier tapa (Lid) que obstaculice su caida, destruyendola al contacto.
 * Visualmente, se distingue por un triangulo blanco centrado en su base.
 * 
 * @author Carlos Sanchez, Samuel Argalle
 * @version 3.0
 */
public class OpenerCup extends Cup {

    private Triangle tooth;

    /**
     * Crea una OpenerCup con el numero y tamano dados.
     * 
     * @param number el numero de la taza
     * @param size   pixeles por cm para la representacion
     */
    public OpenerCup(int number, int size) {
        super(number, size);
        tooth = new Triangle();
        tooth.changeColor("white"); // Blanco no esta en la paleta de colores
        tooth.changeSize(size, size);
    }

    @Override
    public StackableItem createCopy() {
        return new OpenerCup(this.number, this.size);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        // Centrar el triangulo sobre el bloque de la base de la taza
        int centerX = x + (this.number * this.size) - size / 2;
        int baseY = y + (this.getHeight() * this.size) - this.size;
        tooth.setPosition(centerX, baseY);
    }

    @Override
    public void makeVisible() {
        super.makeVisible();
        if (isVisible) tooth.makeVisible();
    }

    @Override
    public void makeInvisible() {
        super.makeInvisible();
        tooth.makeInvisible();
    }

    @Override
    public void executeInsertionEffect(java.util.ArrayList<StackableItem> items, Tower tower) {
        // Destruye solo las tapas con las que colisiona fisicamente al caer.
        // Repite hasta que descanse sobre una taza o el suelo.
        StackableItem restingOn = tower.getRestingOn(this);
        while (restingOn != null && restingOn.isLid()) {
            Lid lid = (Lid) restingOn;
            lid.makeInvisible();
            // Si la tapa estaba cubriendo su taza, liberarla
            for (StackableItem it : new java.util.ArrayList<>(items)) {
                if (it.isCup()) {
                    Cup cup = (Cup) it;
                    if (cup.getNumber() == lid.getNumber() && cup.getLid() == lid) {
                        cup.removeLid();
                    }
                }
            }
            items.remove(lid);
            restingOn = tower.getRestingOn(this);
        }
    }
}
