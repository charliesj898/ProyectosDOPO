package tower;
import shapes.*;
/**
 * Representa una taza en el simulador de torre de apilamiento.
 * Cada taza tiene un nÃºmero, y su altura se calcula como 2*nÃºmero - 1.
 * 
 * @author Carlos Sanchez, Samuel Argalle
 * @version 2.0
 */
public class Cup extends StackableItem {

    private int cupHeight;
    private Rectangle base;
    private Rectangle leftWall;
    private Rectangle rightWall;
    private Lid lid;
    private int baseX, baseY;
    private int leftX, leftY;
    private int rightX, rightY;

    /**
     * Crea una taza con el nÃºmero y tamaÃ±o dados.
     * 
     * @param number el nÃºmero de la taza (determina la altura: 2*nÃºmero - 1)
     * @param size   pÃ­xeles por cm para la representaciÃ³n visual
     */
    public Cup(int number, int size) {
        super(number, size);
        this.cupHeight = 2 * number - 1;
        this.lid = null;

        int totalHeight = cupHeight * size;
        int totalWidth = number * size * 2;

        this.base = new Rectangle();
        base.changeColor(color);
        base.changeSize(size, totalWidth);

        this.leftWall = new Rectangle();
        leftWall.changeColor(color);
        leftWall.changeSize(totalHeight, size);

        this.rightWall = new Rectangle();
        rightWall.changeColor(color);
        rightWall.changeSize(totalHeight, size);

        this.baseX = 70;
        this.baseY = 15;
        this.leftX = 70;
        this.leftY = 15;
        this.rightX = 70;
        this.rightY = 15;
    }

    /**
     * Retorna la altura de la taza en cm.
     * 
     * @return altura de la taza en cm
     */
    @Override
    public int getHeight() {
        return cupHeight;
    }

    /**
     * Coloca una tapa sobre esta taza.
     * 
     * @param lid la tapa a colocar sobre la taza
     */
    public void setLid(Lid lid) {
        this.lid = lid;
        if (this.lid != null) {
            this.lid.setAsCovering(true);
        }
    }

    /**
     * Elimina y retorna la tapa de esta taza.
     * 
     * @return la tapa que estaba en la taza, o null si no tiene
     */
    public Lid removeLid() {
        Lid temp = lid;
        lid = null;
        if (temp != null)
            temp.setAsCovering(false);
        return temp;
    }

    /**
     * Retorna la tapa de esta taza.
     * 
     * @return la tapa, o null si no tiene
     */
    public Lid getLid() {
        return lid;
    }

    /**
     * Retorna si esta taza tiene tapa.
     * 
     * @return true si la taza tiene tapa
     */
    public boolean hasCover() {
        return lid != null;
    }

    /**
     * Establece la posiciÃ³n de la taza en el canvas.
     * 
     * @param x posiciÃ³n horizontal en pÃ­xeles
     * @param y posiciÃ³n vertical en pÃ­xeles
     */
    @Override
    public void setPosition(int x, int y) {
        int totalWidth = this.number * this.size * 2;
        int totalHeight = cupHeight * this.size;

        base.moveHorizontal(x - baseX);
        base.moveVertical((y + totalHeight - this.size) - baseY);
        baseX = x;
        baseY = y + totalHeight - this.size;

        leftWall.moveHorizontal(x - leftX);
        leftWall.moveVertical(y - leftY);
        leftX = x;
        leftY = y;

        rightWall.moveHorizontal((x + totalWidth - this.size) - rightX);
        rightWall.moveVertical(y - rightY);
        rightX = x + totalWidth - this.size;
        rightY = y;

        this.xPosition = x;
        this.yPosition = y;
    }

    /**
     * Hace visible la taza en el canvas.
     */
    @Override
    public void makeVisible() {
        isVisible = true;
        base.makeVisible();
        leftWall.makeVisible();
        rightWall.makeVisible();
    }

    /**
     * Hace invisible la taza en el canvas.
     */
    @Override
    public void makeInvisible() {
        isVisible = false;
        base.makeInvisible();
        leftWall.makeInvisible();
        rightWall.makeInvisible();
    }

    @Override
    public boolean isCup() {
        return true;
    }

    @Override
    public StackableItem createCopy() {
        return new Cup(this.number, this.size);
    }

    public boolean canBeRemoved(int contactY) {
        return true;
    }

    public void executeInsertionEffect(java.util.ArrayList<StackableItem> items, Tower tower) {
        // Efecto por defecto: no hace nada
    }
}
