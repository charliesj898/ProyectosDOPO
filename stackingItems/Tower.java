import java.util.ArrayList;

/**
 * Representa una torre de apilamiento de tazas y tapas.
 * Permite agregar, eliminar y reorganizar tazas y tapas en la torre.
 * @author Carlos Sanchez, Samuel Argalle
 * @version 1.0
 */

public class Tower {
    
    private int width;
    private int maxHeight;
    private boolean isVisible;
    private int currentHeight;
    private boolean lastOk;
    private ArrayList<Cup> cups;
    private ArrayList<Lid> lids;
    private ArrayList<String> stackOrder;
    
    /**
     * Crea una torre con el ancho y alto máximo dados.
     * @param width ancho de la torre en cm
     * @param maxHeight altura máxima de la torre en cm
     */
    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.isVisible = false;
        this.currentHeight = 0;
        this.lastOk = true;
        this.cups = new ArrayList<Cup>();
        this.lids = new ArrayList<Lid>();
        this.stackOrder = new ArrayList<String>();
    }
    
    /**
     * Agrega una taza a la cima de la torre.
     * Solo se agrega si hay espacio y no existe ya una taza con ese número.
     * @param i número de la taza a agregar
     */
    public void pushCup(int i) {
    Cup newCup = new Cup(i, 10);
    if (cupExists(i) || newCup.getHeight() > maxHeight) {
        lastOk = false;
        return;
    }
    cups.add(newCup);
    stackOrder.add("cup" + i);
    currentHeight = calculateHeight();
    redrawTower();
    lastOk = true;
    }
    
    /**
     * Elimina la última taza agregada a la torre.
     * Si la taza tiene tapa, también elimina la tapa.
     */
    public void popCup() {
    Cup lastCup = getLastCup();
    if (lastCup == null) {
        lastOk = false;
        return;
    }
    if (lastCup.hasCover()) {
        Lid lid = lastCup.removeLid();
        lid.makeInvisible();
        lids.remove(lid);
        stackOrder.remove("lid" + lid.getNumber());
    }
    lastCup.makeInvisible();
    cups.remove(lastCup);
    stackOrder.remove("cup" + lastCup.getNumber());
    currentHeight = calculateHeight();
    redrawTower();
    lastOk = true;
    }
    
    /**
     * Elimina una taza específica de la torre por su número.
     * Si la taza tiene tapa, también elimina la tapa.
     * @param i número de la taza a eliminar
     */
    public void removeCup(int i) {
    Cup cup = findCup(i);
    if (cup == null) {
        lastOk = false;
        return;
    }
    if (cup.hasCover()) {
        Lid lid = cup.removeLid();
        lid.makeInvisible();
        lids.remove(lid);
        stackOrder.remove("lid" + lid.getNumber());
    }
    cup.makeInvisible();
    cups.remove(cup);
    stackOrder.remove("cup" + i);
    currentHeight = calculateHeight();
    redrawTower();
    lastOk = true;
    }
    
    /**
     * Agrega una tapa a la torre.
     * Solo se agrega si hay espacio y no existe ya una tapa con ese número.
     * Si existe la taza correspondiente, la tapa se asocia a ella.
     * @param i número de la tapa a agregar
     */
    public void pushLid(int i) {
    if (lidExists(i) || currentHeight + 1 > maxHeight) {
        lastOk = false;
        return;
    }
    Lid newLid = new Lid(i, 10);
    Cup cup = findCup(i);
    if (cup != null) {
        cup.setLid(newLid);
    }
    lids.add(newLid);
    stackOrder.add("lid" + i);
    currentHeight = calculateHeight();
    redrawTower();
    lastOk = true;
    }
    
    /**
     * Elimina la última tapa agregada a la torre.
     */
    public void popLid() {
        Lid lastLid = getLastLid();
        if (lastLid == null) {
            lastOk = false;
            return;
        }
        Cup cup = findCup(lastLid.getNumber());
        if (cup != null) {
            cup.removeLid();
        }
        lastLid.makeInvisible();
        lids.remove(lastLid);
        stackOrder.remove("lid" + lastLid.getNumber());
        currentHeight = calculateHeight();
        redrawTower();
        lastOk = true;
    }
    
    /**
     * Elimina una tapa específica de la torre por su número.
     * @param i número de la tapa a eliminar
     */
    public void removeLid(int i) {
        Lid lid = findLid(i);
        if (lid == null) {
            lastOk = false;
            return;
        }
        Cup cup = findCup(i);
        if (cup != null) {
            cup.removeLid();
        }
        lid.makeInvisible();
        lids.remove(lid);
        stackOrder.remove("lid" + i);
        currentHeight = calculateHeight();
        redrawTower();
        lastOk = true;
    }
    
    /**
     * Retorna la altura actual de los elementos apilados en cm.
     * @return altura actual en cm
     */
    public int height() {
        return currentHeight;
    }
    
    /**
     * Retorna los números de las tazas que tienen tapa,
     * ordenados de menor a mayor.
     * @return arreglo con los números de las tazas tapadas
     */
    public int[] lidedCups() {
        ArrayList<Integer> lidedList = new ArrayList<Integer>();
        for (Cup cup : cups) {
            if (cup.hasCover()) {
                lidedList.add(cup.getNumber());
            }
        }
        //No retornamos este ArrayList porque en los requisitos de diseño pide retornar int[]
        int[] result = new int[lidedList.size()];
        for (int i = 0; i < lidedList.size(); i++) {
            result[i] = lidedList.get(i);
        }
        return result;
    }
    
    /**
     * Retorna los elementos apilados de base a cima.
     * Cada elemento es un arreglo de dos strings: tipo y número.
     * Ejemplo: {{"cup","4"},{"lid","4"}}
     * @return arreglo bidimensional con tipo y número de cada elemento
     */
    public String[][] stackingItems() {
        String[][] result = new String[stackOrder.size()][2];
        for (int i = 0; i < stackOrder.size(); i++) {
            String item = stackOrder.get(i);
            if (item.startsWith("cup")) {
                result[i][0] = "cup";
                result[i][1] = item.substring(3); //corta el texto desde 3 hasta el final (la posicion)
            } else {
                result[i][0] = "lid";
                result[i][1] = item.substring(3);
            }
        }
        return result;
    }
    
    /**
     * Ordena los elementos de la torre de mayor a menor.
     * Solo incluye los elementos que quepan en la altura máxima.
     * El número menor queda en la cima. Si una taza tiene tapa,
     * la tapa se coloca sobre la taza.
     */
    public void orderTower() {
        ArrayList<Cup> sortedCups = new ArrayList<Cup>(cups);
        sortedCups.sort((a, b) -> b.getNumber() - a.getNumber());
        cups.clear();
        lids.clear();
        stackOrder.clear();
        currentHeight = 0;
        for (Cup cup : sortedCups) {
            if (currentHeight + cup.getHeight() <= maxHeight) {
                cups.add(cup);
                stackOrder.add("cup" + cup.getNumber());
                currentHeight = calculateHeight();
                if (cup.hasCover()) {
                    if (currentHeight + 1 <= maxHeight) {
                        lids.add(cup.getLid());
                        stackOrder.add("lid" + cup.getNumber());
                        currentHeight = calculateHeight();
                    } else {
                        cup.removeLid();
                    }
                }
            }
        }
        redrawTower();
        lastOk = true;
    }
    
    /**
     * Invierte el orden de los elementos de la torre.
     * Solo incluye los elementos que quepan en la altura máxima.
     */
    public void reverseTower() {
        ArrayList<Cup> reversedCups = new ArrayList<Cup>(cups);
        java.util.Collections.reverse(reversedCups);
        cups.clear();
        lids.clear();
        stackOrder.clear();
        currentHeight = 0;
        for (Cup cup : reversedCups) {
            if (currentHeight + cup.getHeight() <= maxHeight) {
                cups.add(cup);
                stackOrder.add("cup" + cup.getNumber());
                currentHeight = calculateHeight();
                if (cup.hasCover()) {
                    if (currentHeight + 1 <= maxHeight) {
                        lids.add(cup.getLid());
                        stackOrder.add("lid" + cup.getNumber());
                        currentHeight = calculateHeight();;
                    } else {
                        cup.removeLid();
                    }
                }
            }
        }
        redrawTower();
        lastOk = true;
    }
    
    /**
     * Hace visible la torre en el canvas.
     */
    public void makeVisible() {
    isVisible = true;
    redrawTower();
    }   
    
    /**
     * Hace invisible la torre en el canvas.
     */
    public void makeInvisible() {
    isVisible = false;
    for (Cup cup : cups) {
        cup.makeInvisible();
    }
    for (Lid lid : lids) {
        lid.makeInvisible();
    }
    }
    
    /**
     * Termina el simulador.
     */
    public void exit() {
        makeInvisible();
        System.exit(0);
    }
    
    /**
     * Indica si la última operación se realizó con éxito.
     * @return true si la última operación fue exitosa
     */
    public boolean ok() {
        return lastOk;
    }
    
    // ---------- métodos privados auxiliares ----------
    
    /**
     * Busca una taza por su número.
     * @param number número de la taza a buscar
     * @return la taza encontrada, o null si no existe
     */
    private Cup findCup(int number) {
        for (Cup cup : cups) {
            if (cup.getNumber() == number) {
                return cup;
            }
        }
        return null;
    }
    
    /**
     * Busca una tapa por su número.
     * @param number número de la tapa a buscar
     * @return la tapa encontrada, o null si no existe
     */
    private Lid findLid(int number) {
        for (Lid lid : lids) {
            if (lid.getNumber() == number) {
                return lid;
            }
        }
        return null;
    }
    
    /**
     * Verifica si ya existe una taza con ese número en la torre.
     * @param number número a verificar
     * @return true si ya existe
     */
    private boolean cupExists(int number) {
        return findCup(number) != null;
    }
    
    /**
     * Verifica si ya existe una tapa con ese número en la torre.
     * @param number número a verificar
     * @return true si ya existe
     */
    private boolean lidExists(int number) {
        return findLid(number) != null;
    }
    
    /**
     * Retorna la última taza agregada a la torre.
     * @return la última taza, o null si no hay tazas
     */
    private Cup getLastCup() {
        for (int i = stackOrder.size() - 1; i >= 0; i--) {
            if (stackOrder.get(i).startsWith("cup")) {
                int number = Integer.parseInt(stackOrder.get(i).substring(3));
                return findCup(number);
            }
        }
        return null;
    }
    
    /**
     * Retorna la última tapa agregada a la torre.
     * @return la última tapa, o null si no hay tapas
     */
    private Lid getLastLid() {
        for (int i = stackOrder.size() - 1; i >= 0; i--) {
            if (stackOrder.get(i).startsWith("lid")) {
                int number = Integer.parseInt(stackOrder.get(i).substring(3));
                return findLid(number);
            }
        }
        return null;
    }
    /**
     * Recalcula y redibuја la posición de todos los elementos en el canvas.
     */
    private void redrawTower() {
    int size = 10;
    int canvasCenter = 80;
    int yBase = 280;
    int currentY = yBase;
    int lastCupHeight = 0;

    for (String item : stackOrder) {
        if (item.startsWith("cup")) {
            int number = Integer.parseInt(item.substring(3));
            Cup cup = findCup(number);
            int cupWidth = cup.getNumber() * size * 2;
            int xPos = canvasCenter - cupWidth / 2;
            if (cup.getHeight() > lastCupHeight) {
                // es mas grande, se apoya encima
                currentY -= cup.getHeight() * size;
                lastCupHeight = cup.getHeight();
            }
            // si es mas pequeña, currentY no cambia, va en la misma posicion
            cup.setPosition(xPos, currentY);
            if (isVisible) cup.makeVisible();
        } else {
    int number = Integer.parseInt(item.substring(3));
    Lid lid = findLid(number);
    int lidWidth = number * size * 2;
    int xPos = canvasCenter - lidWidth / 2;
    Cup cup = findCup(number);
    if (cup != null && cup.hasCover()) {
        // tapa encima de su taza
        int yPos = currentY - size;
        lid.setPosition(xPos, yPos);
    } else {
        // tapa sola, va apilada en el orden que le corresponde
        currentY -= size;
        lid.setPosition(xPos, currentY);
    }
    if (isVisible) lid.makeVisible();
    }
    }
    }
    
    /**
     * Calcula la altura actual de la torre .
     * @return altura máxima en cm
     */
    /**
     * Calcula la altura actual de la torre.
     * Las tazas más pequeñas que la anterior no suman altura porque caben dentro.
     * Las tazas más grandes se apoyan sobre las paredes de la anterior y suman su altura completa.
     * Las tapas solo suman 1cm si están encima de su taza.
     * @return altura total de la torre en cm
     */
    private int calculateHeight() {
    int total = 0;
    int lastCupHeight = 0; // altura de la ultima taza procesada
    for (String item : stackOrder) {
        if (item.startsWith("cup")) {
            int number = Integer.parseInt(item.substring(3));
            Cup cup = findCup(number);
            if (cup.getHeight() > lastCupHeight) {
                // la taza es mas grande que la anterior
                // se apoya sobre sus paredes y suma su altura completa
                total += cup.getHeight();
            }
            // actualizamos la altura de la ultima taza sin importar si es mayor o menor
            lastCupHeight = cup.getHeight();
        } else {
            int number = Integer.parseInt(item.substring(3));
            Cup cup = findCup(number);
            if (cup != null && cup.hasCover()) {
                // la tapa esta encima de su taza, suma 1cm
                total += 1;
            }
            // si la tapa esta sola no suma altura porque no hay taza debajo
        }
    }
    return total;
    }
}