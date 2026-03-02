/**
 * Representa una tapa en el simulador de torre de apilamiento.
 * Cada tapa tiene un número que corresponde a su taza, y siempre mide 1 cm de
 * alto.
 * 
 * @author Carlos Sanchez, Samuel Argalle
 * @version 2.0
 */
public class Lid {

    private static final String[] COLORS = {
            "red", "blue", "green", "yellow", "magenta", "orange", "cyan", "black"
    };
    private static final int STANDARD_HEIGHT = 1;

    private int cupNumber;
    private int size;
    private int number;
    private String color;
    private int xPosition;
    private int yPosition;
    private boolean isVisible;
    private boolean isCovering;
    private Rectangle shape;
    private Rectangle innerLine;

    /**
     * Crea una tapa con el número de taza, tamaño y color dados.
     * 
     * @param cupNumber el número de la taza a la que pertenece esta tapa
     * @param size      píxeles por cm para la representación visual
     * @param color     el color de la tapa (debe ser igual al de su taza)
     */
    public Lid(int cupNumber, int size) {
        this.cupNumber = cupNumber;
        this.size = size;
        this.number = cupNumber;
        this.color = COLORS[cupNumber % COLORS.length];
        this.xPosition = 70;
        this.yPosition = 15;
        this.isVisible = false;
        this.isCovering = false;
        this.shape = new Rectangle();
        shape.changeColor(color);
        shape.changeSize(STANDARD_HEIGHT * size, cupNumber * size * 2);

        this.innerLine = new Rectangle();
        innerLine.changeColor("black");
        innerLine.changeSize(2, cupNumber * size * 2); // 2 pixeles de grosor para la linea negra central
    }

    /**
     * Retorna la altura de la tapa.
     * 
     * @return altura de la tapa
     */
    public int getHeight() {
        return STANDARD_HEIGHT;
    }

    /**
     * Retorna el número de la taza a la que pertenece esta tapa.
     * 
     * @return número de la taza
     */
    public int getCupNumber() {
        return cupNumber;
    }

    /**
     * Retorna el número de la tapa.
     * 
     * @return número de la tapa
     */
    public int getNumber() {
        return number;
    }

    /**
     * Retorna el tamaño en píxeles por cm.
     * 
     * @return tamaño
     */
    public int getSize() {
        return size;
    }

    /**
     * Retorna el color de la tapa.
     * 
     * @return color en String
     */
    public String getColor() {
        return color;
    }

    /**
     * Establece la posición de la tapa en el canvas.
     * 
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
        innerLine.moveHorizontal(dx);
        innerLine.moveVertical(dy + (STANDARD_HEIGHT * size) / 2 - 1);
    }

    /**
     * Define visualmente si la tapa esta cubriendo a su taza, activando
     * el indicador visual extra.
     * 
     * @param covering true si cubre a la taza principal
     */
    public void setAsCovering(boolean covering) {
        this.isCovering = covering;
        if (isVisible) {
            if (covering)
                innerLine.makeVisible();
            else
                innerLine.makeInvisible();
        }
    }

    /**
     * Hace visible la tapa en el canvas.
     */
    public void makeVisible() {
        isVisible = true;
        shape.makeVisible();
        if (isCovering)
            innerLine.makeVisible();
    }

    /**
     * Hace invisible la tapa en el canvas.
     */
    public void makeInvisible() {
        isVisible = false;
        shape.makeInvisible();
        innerLine.makeInvisible();
    }
}