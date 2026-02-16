/**
 * Representa una tapa en el simulador de torre de apilamiento.
 * Cada tapa tiene un número que corresponde a su taza, y siempre mide 1 cm de alto.
 * @author Carlos Sanchez, Samuel Argalle
 * @version 1.0
 */
public class Lid {
    
    private static final String[] COLORS = {"red", "blue", "green", "yellow", "magenta"};
    private static final int STANDARD_HEIGHT = 1;
    
    private int cupNumber;
    private int size;
    private int number;
    private String color;
    private int xPosition;
    private int yPosition;
    private boolean isVisible;
    private Rectangle shape;
    
    /**
     * Crea una tapa con el número de taza, tamaño y color dados.
     * @param cupNumber el número de la taza a la que pertenece esta tapa
     * @param size píxeles por cm para la representación visual
     * @param color el color de la tapa (debe ser igual al de su taza)
     */
    public Lid(int cupNumber, int size) {
    this.cupNumber = cupNumber;
    this.size = size;
    this.number = cupNumber;
    this.color = COLORS[cupNumber % COLORS.length];
    this.xPosition = 0;
    this.yPosition = 0;
    this.isVisible = false;
    this.shape = new Rectangle();
    shape.changeColor(color);
    shape.changeSize(STANDARD_HEIGHT * size, cupNumber * size * 2);
    }
    
    /**
     * Retorna la altura de la tapa en cm (siempre 1).
     * @return altura de la tapa en cm
     */
    public int getHeight() {
        return STANDARD_HEIGHT;
    }
    
    /**
     * Retorna el número de la taza a la que pertenece esta tapa.
     * @return número de la taza
     */
    public int getCupNumber() {
        return cupNumber;
    }
    
    /**
     * Retorna el número de la tapa.
     * @return número de la tapa
     */
    public int getNumber() {
        return number;
    }
    
    /**
     * Retorna el tamaño en píxeles por cm.
     * @return tamaño
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Retorna el color de la tapa.
     * @return color en String
     */
    public String getColor() {
        return color;
    }
    
    /**
     * Establece la posición de la tapa en el canvas.
     * @param x posición horizontal en píxeles
     * @param y posición vertical en píxeles
     */
    public void setPosition(int x, int y) {
    int dx = x - this.xPosition;
    int dy = y - this.yPosition;
    this.xPosition = x;
    this.yPosition = y;
    shape.moveHorizontal(dx);
    shape.moveVertical(dy);
    }
    
    /**
     * Hace visible la tapa en el canvas.
     */
    public void makeVisible() {
        isVisible = true;
        shape.makeVisible();
    }
    
    /**
     * Hace invisible la tapa en el canvas.
     */
    public void makeInvisible() {
        isVisible = false;
        shape.makeInvisible();
    }
}