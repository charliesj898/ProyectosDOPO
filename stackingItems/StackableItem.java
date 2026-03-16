/**
 * Clase base abstracta para todos los elementos apilables de la torre.
 * Provee estado comun como el numero, identificacion de color y control de visibilidad.
 * 
 * @author Carlos Sanchez, Samuel Argalle 
 * @version 3.0
 */
public abstract class StackableItem implements DrawableItem {
    
    // Paleta de colores compartida por tazas y tapas
    protected static final String[] COLORS = {
        "red", "blue", "green", "yellow", "magenta", "orange", "cyan", "black"
    };

    protected int number;
    protected int size;
    protected String color;
    protected boolean isVisible;
    protected int xPosition; // Posicion X logica compartida
    protected int yPosition; // Posicion Y logica compartida

    /**
     * Constructor base para elementos apilables
     * 
     * @param number identificador numerico del elemento
     * @param size   tamano base de renderizado (pixeles por cm)
     */
    public StackableItem(int number, int size) {
        this.number = number;
        this.size = size;
        this.color = COLORS[number % COLORS.length];
        this.isVisible = false;
        // Posiciones por defecto en el simulador
        this.xPosition = 70;
        this.yPosition = 15;
    }

    /**
     * Retorna el numero del item.
     * 
     * @return numero identificador
     */
    public int getNumber() {
        return number;
    }

    /**
     * Retorna la constante de escala (pixeles por cm).
     * 
     * @return tamano base
     */
    public int getSize() {
        return size;
    }

    /**
     * Retorna el color asignado a este item.
     * 
     * @return nombre del color
     */
    public String getColor() {
        return color;
    }
    
    /**
     * Retorna la altura del item en cm.
     * Al ser abstracta, cada subclase (taza, tapa) define su propia regla de altura.
     * 
     * @return altura en cm
     */
    public abstract int getHeight();
}