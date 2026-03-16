/**
 * Define el contrato visual para cualquier elemento de la torre
 * que pueda ser representado graficamente en el Canvas.
 * 
 * @author Carlos Sanchez, Samuel Argalle
 * @version 3.0
 */
public interface DrawableItem {

    /**
     * Hace visible el item en el canvas.
     */
    void makeVisible();

    /**
     * Hace invisible el item en el canvas.
     */
    void makeInvisible();

    /**
     * Establece la posicion del item en el canvas.
     * 
     * @param x posicion horizontal en pixeles (esquina superior izquierda)
     * @param y posicion vertical en pixeles (esquina superior izquierda)
     */
    void setPosition(int x, int y);
}
