package tower;

import shapes.Triangle;

/**
 * Tapa loca. Se comporta como una tapa normal, pero si llega a tapar a su taza,
 * se introduce por debajo de ella empujandola hacia arriba 1 cm.
 * Se distingue visualmente por un triangulo blanco centrado en la tapa.
 *
 * @author Carlos Sanchez, Samuel Argalle
 * @version 3.0
 */
public class CrazyLid extends Lid {

    private Triangle crazyHat;

    public CrazyLid(int number, int size) {
        super(number, size);
        crazyHat = new Triangle();
        crazyHat.changeColor("white"); // Color no usado
        crazyHat.changeSize(size, size);
    }

    @Override
    public StackableItem createCopy() {
        return new CrazyLid(this.number, this.size);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        // Centro horizontal de la tapa, dentro del 1cm de altura
        int centerX = x + (this.number * this.size) - size / 2;
        crazyHat.setPosition(centerX, y);
    }

    @Override
    public void makeVisible() {
        super.makeVisible();
        if (isVisible) crazyHat.makeVisible();
    }

    @Override
    public void makeInvisible() {
        super.makeInvisible();
        crazyHat.makeInvisible();
    }

    @Override
    public boolean isCrazyLid() {
        return true;
    }
}
