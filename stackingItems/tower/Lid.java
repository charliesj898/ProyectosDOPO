package tower;
import shapes.*;
/**
 * Representa una tapa en el simulador de torre de apilamiento.
 * Cada tapa tiene un nÃºmero que corresponde a su taza, y siempre mide 1 cm de
 * alto.
 * 
 * @author Carlos Sanchez, Samuel Argalle
 * @version 2.0
 */
public class Lid extends StackableItem {

    private static final int STANDARD_HEIGHT = 1;

    private int cupNumber;
    private boolean isCovering;
    private Rectangle shape;
    private Rectangle innerLine;

    /**
     * Crea una tapa con el nÃºmero de taza, tamaÃ±o y color dados.
     * 
     * @param cupNumber el nÃºmero de la taza a la que pertenece esta tapa
     * @param size      pÃ­xeles por cm para la representaciÃ³n visual
     * @param color     el color de la tapa (debe ser igual al de su taza)
     */
    public Lid(int cupNumber, int size) {
        super(cupNumber, size);
        this.cupNumber = cupNumber;
        this.isCovering = false;
        
        this.shape = new Rectangle();
        shape.changeColor(this.color);
        shape.changeSize(STANDARD_HEIGHT * size, cupNumber * size * 2);

        this.innerLine = new Rectangle();
        innerLine.changeColor("black");
        innerLine.changeSize(2, cupNumber * size * 2); // 2 pixeles de grosor para la linea negra central
        // Desplazala hacia abajo matematicamente UNA VEZ durante su creacion para que su origen de coordenadas
        // local encaje perfectamente en el centro del rectangulo de la tapa.
        innerLine.moveVertical((STANDARD_HEIGHT * size) / 2 - 1);
    }

    /**
     * Retorna la altura de la tapa.
     * 
     * @return altura de la tapa
     */
    @Override
    public int getHeight() {
        return STANDARD_HEIGHT;
    }

    /**
     * Retorna el nÃºmero de la taza a la que pertenece esta tapa.
     * 
     * @return nÃºmero de la taza
     */
    public int getCupNumber() {
        return cupNumber;
    }

    /**
     * Establece la posiciÃ³n de la tapa en el canvas.
     * 
     * @param x posiciÃ³n horizontal en pÃ­xeles
     * @param y posiciÃ³n vertical en pÃ­xeles
     */
    @Override
    public void setPosition(int x, int y) {
        // Calcula los deltas basados en la memoria de la posicion interna anterior
        int dx = x - this.xPosition;
        int dy = y - this.yPosition;
        
        // Actualiza la posicion interna verdadera
        this.xPosition = x;
        this.yPosition = y;
        
        // Mueve la figura principal usando el delta
        shape.moveHorizontal(dx);
        shape.moveVertical(dy);
        
        // Mueve la linea interior exactamente junto con la figura principal por deltas identicos.
        // Mantiene su centro vertical perfecto porque su origen fue desplazado durante la construccion.
        innerLine.moveHorizontal(dx);
        innerLine.moveVertical(dy);
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
    @Override
    public void makeVisible() {
        isVisible = true;
        shape.makeVisible();
        if (isCovering)
            innerLine.makeVisible();
    }

    /**
     * Hace invisible la tapa en el canvas.
     */
    @Override
    public void makeInvisible() {
        isVisible = false;
        shape.makeInvisible();
        innerLine.makeInvisible();
    }

    @Override
    public boolean isCup() {
        return false;
    }

    @Override
    public StackableItem createCopy() {
        return new Lid(this.number, this.size);
    }

    public boolean canBeRemoved(boolean isCoveringExactMaster) {
        return true;
    }

    public boolean canEnterTower(boolean hasMasterCup) {
        return true;
    }

    /**
     * Hook polimórfico llamado por Tower justo después de que la tapa es colocada.
     * Permite a subclases reaccionar al evento de colocación (por ejemplo, autodestruirse).
     * 
     * @param items lista interna de la torre
     */
    public void onAfterPlaced(java.util.ArrayList<StackableItem> items) {
        // No-op por defecto
    }
}
