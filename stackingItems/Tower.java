import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Representa una torre de apilamiento de tazas y tapas.
 * Permite agregar, eliminar y reorganizar tazas y tapas en la torre.
 * 
 * @author Carlos Sanchez, Samuel Argalle
 * @version 2.0
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

    private int pixelsPerCm;
    private Rectangle baseFloor;
    private Rectangle leftWall;
    private Rectangle rightWall;
    private ArrayList<Rectangle> cmMarks;

    /**
     * Crea una torre con el ancho y alto máximo dados.
     * 
     * @param width     ancho de la torre en cm
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

        this.pixelsPerCm = 10;
        int wallThickness = 5;
        int innerWidth = width * pixelsPerCm;
        int innerHeight = maxHeight * pixelsPerCm;

        this.baseFloor = new Rectangle();
        this.baseFloor.changeColor("black");
        this.baseFloor.changeSize(wallThickness, innerWidth + 2 * wallThickness);

        this.leftWall = new Rectangle();
        this.leftWall.changeColor("black");
        this.leftWall.changeSize(innerHeight, wallThickness);

        this.rightWall = new Rectangle();
        this.rightWall.changeColor("black");
        this.rightWall.changeSize(innerHeight, wallThickness);

        this.cmMarks = new ArrayList<Rectangle>();
        for (int i = 1; i <= maxHeight; i++) {
            Rectangle mark = new Rectangle();
            mark.changeColor("black");
            mark.changeSize(2, 10);
            this.cmMarks.add(mark);
        }
    }

    /**
     * Crea una torre e inicializa con el número de tazas indicadas.
     * Crea tazas de 1 a cups (sin tapas). Ej: cups=4 -> crea 1,2,3 y 4.
     * 
     * @param cups cantidad de tazas a crear
     */
    public Tower(int cups) {
        this(cups * 3 + 10, cups * 2 * cups + 10);
        for (int i = 1; i <= cups; i++) {
            pushCup(i);
        }
    }

    /**
     * Agrega una taza a la cima de la torre.
     * Solo se agrega si hay espacio y no existe ya una taza con ese número.
     * 
     * @param i número de la taza a agregar
     */
    public void pushCup(int i) {
        if (cupExists(i)) {
            showError("Ya existe una taza con el numero " + i + " en la torre.");
            lastOk = false;
            return;
        }
        ArrayList<String> backupOrder = new ArrayList<String>(stackOrder);

        Cup newCup = new Cup(i, pixelsPerCm);
        cups.add(newCup);
        stackOrder.add("cup" + i);

        int newHeight = calculateHeight();
        if (newHeight > maxHeight) {
            cups.remove(newCup);
            stackOrder = backupOrder;
            showError("La taza " + i + " no cabe: excede la altura maxima de " + maxHeight + " cm.");
            lastOk = false;
            return;
        }
        currentHeight = newHeight;
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
            showError("No hay tazas en la torre para eliminar.");
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
     * 
     * @param i número de la taza a eliminar
     */
    public void removeCup(int i) {
        Cup cup = findCup(i);
        if (cup == null) {
            showError("No existe una taza con el numero " + i + " en la torre.");
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
     * 
     * @param i número de la tapa a agregar
     */
    public void pushLid(int i) {
        if (lidExists(i)) {
            showError("Ya existe una tapa con el numero " + i + " en la torre.");
            lastOk = false;
            return;
        }
        ArrayList<String> backupOrder = new ArrayList<String>(stackOrder);

        Lid newLid = new Lid(i, pixelsPerCm);
        lids.add(newLid);
        stackOrder.add("lid" + i);

        int newHeight = calculateHeight();
        if (newHeight > maxHeight) {
            lids.remove(newLid);
            stackOrder = backupOrder;
            showError("La tapa " + i + " no cabe: excede la altura maxima de " + maxHeight + " cm.");
            lastOk = false;
            return;
        }
        currentHeight = newHeight;
        redrawTower();
        lastOk = true;
    }

    /**
     * Elimina la última tapa agregada a la torre.
     */
    public void popLid() {
        Lid lastLid = getLastLid();
        if (lastLid == null) {
            showError("No hay tapas en la torre para eliminar.");
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
     * 
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
     * 
     * @return altura actual en cm
     */
    public int height() {
        return currentHeight;
    }

    /**
     * Retorna los números de las tazas que tienen tapa,
     * ordenados de menor a mayor.
     * 
     * @return arreglo con los números de las tazas tapadas
     */
    public int[] lidedCups() {
        ArrayList<Integer> lidedList = new ArrayList<Integer>();
        for (Cup cup : cups) {
            if (cup.hasCover()) {
                lidedList.add(cup.getNumber());
            }
        }
        // No retornamos este ArrayList porque en los requisitos de diseño pide retornar
        // int[]
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
     * 
     * @return arreglo bidimensional con tipo y número de cada elemento
     */
    public String[][] stackingItems() {
        String[][] result = new String[stackOrder.size()][2];
        for (int i = 0; i < stackOrder.size(); i++) {
            String item = stackOrder.get(i);
            if (item.startsWith("cup")) {
                result[i][0] = "cup";
                result[i][1] = item.substring(3); // corta el texto desde 3 hasta el final (la posicion)
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
        // guardamos las tapas solas (sin taza) antes de limpiar
        ArrayList<Lid> soloLids = new ArrayList<Lid>();
        for (Lid lid : lids) {
            if (findCup(lid.getNumber()) == null) {
                soloLids.add(lid);
            }
        }
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
        // re-agregamos las tapas solas al final si caben
        for (Lid lid : soloLids) {
            if (currentHeight + 1 <= maxHeight) {
                lids.add(lid);
                stackOrder.add("lid" + lid.getNumber());
                currentHeight = calculateHeight();
            } else {
                lid.makeInvisible();
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
        // guardamos las tapas solas (sin taza) antes de limpiar
        ArrayList<Lid> soloLids = new ArrayList<Lid>();
        for (Lid lid : lids) {
            if (findCup(lid.getNumber()) == null) {
                soloLids.add(lid);
            }
        }
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
                        currentHeight = calculateHeight();
                    } else {
                        cup.removeLid();
                    }
                }
            }
        }
        // re-agregamos las tapas solas al final si caben
        for (Lid lid : soloLids) {
            if (currentHeight + 1 <= maxHeight) {
                lids.add(lid);
                stackOrder.add("lid" + lid.getNumber());
                currentHeight = calculateHeight();
            } else {
                lid.makeInvisible();
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
        if (baseFloor != null)
            baseFloor.makeVisible();
        if (leftWall != null)
            leftWall.makeVisible();
        if (rightWall != null)
            rightWall.makeVisible();
        if (cmMarks != null) {
            for (Rectangle mark : cmMarks) {
                mark.makeVisible();
            }
        }
        redrawTower();
    }

    /**
     * Hace invisible la torre en el canvas.
     */
    public void makeInvisible() {
        isVisible = false;
        if (baseFloor != null)
            baseFloor.makeInvisible();
        if (leftWall != null)
            leftWall.makeInvisible();
        if (rightWall != null)
            rightWall.makeInvisible();
        if (cmMarks != null) {
            for (Rectangle mark : cmMarks) {
                mark.makeInvisible();
            }
        }
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
     * Intercambia la posicion de dos objetos de la torre.
     * Identificados por su tipo y numero, ej. {"cup", "4"} o {"lid", "4"}
     * 
     * @param o1 objeto 1
     * @param o2 objeto 2
     */
    public void swap(String[] o1, String[] o2) {
        if (o1 == null || o1.length < 2 || o2 == null || o2.length < 2)
            return;
        String s1 = o1[0] + o1[1];
        String s2 = o2[0] + o2[1];

        int idx1 = stackOrder.indexOf(s1);
        int idx2 = stackOrder.indexOf(s2);

        if (idx1 == -1 || idx2 == -1) {
            showError("Uno o ambos elementos no existen en la torre.");
            lastOk = false;
            return;
        }

        java.util.Collections.swap(stackOrder, idx1, idx2);

        int newHeight = calculateHeight();
        if (newHeight > maxHeight) {
            java.util.Collections.swap(stackOrder, idx1, idx2);
            showError("El intercambio excede la altura maxima de la torre.");
            lastOk = false;
        } else {
            currentHeight = newHeight;
            redrawTower();
            lastOk = true;
        }
    }

    /**
     * Permite tapar las tazas que tienen sus tapas en la torre.
     */
    public void cover() {
        ArrayList<String> newOrder = new ArrayList<>();
        ArrayList<String> strayLids = new ArrayList<>();

        for (Lid lid : lids) {
            if (findCup(lid.getNumber()) != null) {
                strayLids.add("lid" + lid.getNumber());
            }
        }

        for (String item : stackOrder) {
            if (item.startsWith("cup")) {
                newOrder.add(item);
                String expectedLid = "lid" + item.substring(3);
                if (strayLids.contains(expectedLid)) {
                    newOrder.add(expectedLid);
                }
            } else {
                if (!strayLids.contains(item)) {
                    newOrder.add(item);
                }
            }
        }

        ArrayList<String> backupOrder = stackOrder;
        stackOrder = newOrder;

        int newHeight = calculateHeight();
        if (newHeight > maxHeight) {
            stackOrder = backupOrder;
            showError("La operacion de cubrir excederia la altura.");
            lastOk = false;
        } else {
            currentHeight = newHeight;
            redrawTower();
            lastOk = true;
        }
    }

    /**
     * Consulta un movimiento de intercambio que reduzca la altura de la torre.
     * 
     * @return arreglo con dos objetos a intercambiar, o null
     */
    public String[][] swapToReduce() {
        int actualH = this.calculateHeight();

        for (int i = 0; i < stackOrder.size(); i++) {
            for (int j = i + 1; j < stackOrder.size(); j++) {
                java.util.Collections.swap(stackOrder, i, j);
                int testH = calculateHeight();
                java.util.Collections.swap(stackOrder, i, j);

                if (testH <= maxHeight && testH < actualH) {
                    String[][] best = new String[2][2];
                    String s1 = stackOrder.get(i);
                    String s2 = stackOrder.get(j);
                    best[0][0] = s1.startsWith("cup") ? "cup" : "lid";
                    best[0][1] = s1.substring(3);
                    best[1][0] = s2.startsWith("cup") ? "cup" : "lid";
                    best[1][1] = s2.substring(3);
                    lastOk = true;
                    return best;
                }
            }
        }
        lastOk = true;
        return null;
    }

    /**
     * Indica si la última operación se realizó con éxito.
     * 
     * @return true si la última operación fue exitosa
     */
    public boolean ok() {
        return lastOk;
    }

    // ---------- métodos privados auxiliares ----------

    /**
     * Busca una taza por su número.
     * 
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
     * 
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
     * 
     * @param number número a verificar
     * @return true si ya existe
     */
    private boolean cupExists(int number) {
        return findCup(number) != null;
    }

    /**
     * Verifica si ya existe una tapa con ese número en la torre.
     * 
     * @param number número a verificar
     * @return true si ya existe
     */
    private boolean lidExists(int number) {
        return findLid(number) != null;
    }

    /**
     * Retorna la última taza agregada a la torre.
     * 
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
     * 
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
     * Muestra un mensaje de error al usuario si el simulador esta visible.
     * Solo se muestra si isVisible es true (requisito de usabilidad).
     * 
     * @param message el mensaje a mostrar
     */
    private void showError(String message) {
        if (isVisible) {
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calcula las posiciones en cm (relativa al suelo) para cada item en
     * stackOrder.
     * Utiliza un modelo donde los items se apilan sobre la superficie de
     * intersección
     * más alta.
     */
    private java.util.HashMap<String, Integer> calculatePositions() {
        for (Cup cup : cups)
            cup.removeLid(); // Reset visual covers

        java.util.HashMap<String, Integer> bottoms = new java.util.HashMap<String, Integer>();
        for (String itemStr : stackOrder) {
            if (itemStr.startsWith("cup")) {
                int number = Integer.parseInt(itemStr.substring(3));
                int contactY = 0;
                for (String placedStr : bottoms.keySet()) {
                    int placedY = bottoms.get(placedStr);
                    if (placedStr.startsWith("cup")) {
                        int pNumber = Integer.parseInt(placedStr.substring(3));
                        if (number >= pNumber) {
                            // Nuestra taza es mas ancha o igual: se apoya en los bordes de la taza inferior
                            contactY = Math.max(contactY, placedY + 2 * pNumber - 1);
                        } else {
                            // Nuestra taza es mas estrecha: cabe dentro de la taza inferior,
                            // pero se apoya en el fondo interior (suelo) que tiene 1cm de grosor.
                            contactY = Math.max(contactY, placedY + 1);
                        }
                    } else { // lid
                        contactY = Math.max(contactY, placedY + 1);
                    }
                }
                bottoms.put(itemStr, contactY);
            } else { // lid
                int number = Integer.parseInt(itemStr.substring(3));

                int contactY = 0;
                String restingOn = null;

                for (String placedStr : bottoms.keySet()) {
                    int placedY = bottoms.get(placedStr);
                    int testY = 0;
                    if (placedStr.startsWith("cup")) {
                        int pNumber = Integer.parseInt(placedStr.substring(3));
                        if (number >= pNumber) {
                            testY = placedY + 2 * pNumber - 1;
                        } else {
                            testY = placedY + 1;
                        }
                    } else { // lid
                        testY = placedY + 1;
                    }

                    if (testY > contactY) {
                        contactY = testY;
                        restingOn = placedStr;
                    } else if (testY == contactY) {
                        if (placedStr.equals("cup" + number))
                            restingOn = placedStr;
                        else if (restingOn == null)
                            restingOn = placedStr;
                    }
                }
                bottoms.put(itemStr, contactY);

                if (restingOn != null && restingOn.equals("cup" + number)) {
                    Cup cup = findCup(number);
                    if (cup != null) {
                        cup.setLid(findLid(number));
                        if (isVisible)
                            cup.makeVisible();
                    }
                }
            }
        }
        return bottoms;
    }

    /**
     * Recalcula y dibuja la posicion de todos los elementos en el canvas.
     */
    private void redrawTower() {
        int canvasCenter = 150;
        int groundY = 280;

        java.util.HashMap<String, Integer> bottoms = calculatePositions();

        for (String itemStr : stackOrder) {
            int simY = bottoms.get(itemStr);
            if (itemStr.startsWith("cup")) {
                int number = Integer.parseInt(itemStr.substring(3));
                Cup cup = findCup(number);

                int cupWidth = number * pixelsPerCm * 2;
                int xPos = canvasCenter - cupWidth / 2;
                int cupHeightCm = 2 * number - 1;
                int topSimY = simY + cupHeightCm;

                int yPos = groundY - (topSimY * pixelsPerCm);

                cup.setPosition(xPos, yPos);
                if (isVisible)
                    cup.makeVisible();
            } else {
                int number = Integer.parseInt(itemStr.substring(3));
                Lid lid = findLid(number);

                int lidWidth = number * pixelsPerCm * 2;
                int xPos = canvasCenter - lidWidth / 2;
                int topSimY = simY + 1; // lid height is 1

                int yPos = groundY - (topSimY * pixelsPerCm);

                lid.setPosition(xPos, yPos);
                if (isVisible)
                    lid.makeVisible();
            }
        }

        // Draw the tower container boundaries and cm marks
        if (baseFloor != null) {
            int wallThickness = 5;
            int innerWidth = width * pixelsPerCm;
            int innerHeight = maxHeight * pixelsPerCm;

            baseFloor.setPosition(canvasCenter - innerWidth / 2 - wallThickness, groundY);
            leftWall.setPosition(canvasCenter - innerWidth / 2 - wallThickness, groundY - innerHeight);
            rightWall.setPosition(canvasCenter + innerWidth / 2, groundY - innerHeight);

            for (int i = 0; i < cmMarks.size(); i++) {
                Rectangle mark = cmMarks.get(i);
                int markY = groundY - ((i + 1) * pixelsPerCm);
                mark.setPosition(canvasCenter - innerWidth / 2 + 2, markY);
            }

            if (isVisible) {
                baseFloor.makeVisible();
                leftWall.makeVisible();
                rightWall.makeVisible();
                for (Rectangle mark : cmMarks) {
                    mark.makeVisible();
                }
            }
        }
    }

    /**
     * Calcula la altura actual de la torre.
     * 
     * @return altura total de la torre en cm
     */
    private int calculateHeight() {
        int maxH = 0;
        java.util.HashMap<String, Integer> bottoms = calculatePositions();
        for (String itemStr : stackOrder) {
            int y = bottoms.get(itemStr);
            if (itemStr.startsWith("cup")) {
                int number = Integer.parseInt(itemStr.substring(3));
                maxH = Math.max(maxH, y + 2 * number - 1);
            } else {
                maxH = Math.max(maxH, y + 1);
            }
        }
        return maxH;
    }
}