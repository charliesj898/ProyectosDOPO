package tower;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import shapes.*;

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
    private ArrayList<StackableItem> items;

    private int pixelsPerCm;
    private Rectangle baseFloor;
    private Rectangle leftWall;
    private Rectangle rightWall;
    private ArrayList<Rectangle> cmMarks;

    /**
     * Crea una torre con el ancho y alto maximo dados.
     * 
     * @param width     ancho de la torre en cm
     * @param maxHeight altura maxima de la torre en cm
     */
    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.isVisible = false;
        this.currentHeight = 0;
        this.lastOk = true;
        this.items = new ArrayList<StackableItem>();

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
     * Crea una torre e inicializa con el numero de tazas indicadas.
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
     * Agrega una taza normal a la cima de la torre.
     *
     * @param i numero de la taza a agregar
     */
    public void pushCup(int i) {
        pushCup(i, "normal");
    }

    /**
     * Agrega una taza a la cima de la torre segun su tipo.
     *
     * @param i    numero de la taza a agregar
     * @param type tipo de la taza: "normal", "opener", "hierarchical"
     */
    public void pushCup(int i, String type) {
        if (2 * i > this.width) {
            showError("La taza " + i + " no cabe: su ancho (" + (2 * i) + " cm) excede el ancho de la torre ("
                    + this.width + " cm).");
            lastOk = false;
            return;
        }

        if (cupExists(i)) {
            showError("Ya existe una taza con el numero " + i + " en la torre.");
            lastOk = false;
            return;
        }

        Cup newCup;
        if (type.equalsIgnoreCase("opener")) {
            newCup = new OpenerCup(i, pixelsPerCm);
        } else if (type.equalsIgnoreCase("hierarchical")) {
            newCup = new HierarchicalCup(i, pixelsPerCm);
        } else {
            newCup = new Cup(i, pixelsPerCm);
        }
        items.add(newCup);

        // Logica de efectos de insercion polimorfica

        newCup.executeInsertionEffect(items, this);

        int newHeight = calculateHeight();
        // Nota: Al destruir tapas, la altura podria REDUCIRSE, asegurando que entre

        if (newHeight > maxHeight) {
            items.remove(items.size() - 1); // remove the newly added cup
            showError("La taza " + i + " no cabe: excede la altura maxima de " + maxHeight + " cm.");
            lastOk = false;
            return;
        }
        currentHeight = newHeight;
        redrawTower();
        lastOk = true;
    }

    /**
     * Elimina la ultima taza agregada a la torre.
     * Si la taza tiene tapa, tambien elimina la tapa.
     */
    public void popCup() {
        Cup lastCup = getLastCup();
        if (lastCup == null) {
            showError("No hay tazas en la torre para eliminar.");
            lastOk = false;
            return;
        }

        // Logica de remocion polimorfica (HierarchicalCup revisa si esta en Y=0)

        java.util.HashMap<StackableItem, Integer> bottoms = calculatePositions();
        int contactY = bottoms.get(lastCup) != null ? bottoms.get(lastCup) : 0;
        if (!lastCup.canBeRemoved(contactY)) {
            showError("La taza jerarquica se ha fijado en el suelo y rechaza ser eliminada.");
            lastOk = false;
            return;
        }

        if (lastCup.hasCover()) {
            Lid lid = lastCup.removeLid();
            lid.makeInvisible();
            items.remove(lid);
        }
        lastCup.makeInvisible();
        items.remove(lastCup);
        currentHeight = calculateHeight();
        redrawTower();
        lastOk = true;
    }

    /**
     * Elimina una taza especifica de la torre por su numero.
     * Si la taza tiene tapa, tambien elimina la tapa.
     * 
     * @param i numero de la taza a eliminar
     */
    public void removeCup(int i) {
        Cup cup = findCup(i);
        if (cup == null) {
            showError("No existe una taza con el numero " + i + " en la torre.");
            lastOk = false;
            return;
        }

        // Logica de remocion polimorfica

        java.util.HashMap<StackableItem, Integer> bottoms = calculatePositions();
        int contactY = bottoms.get(cup) != null ? bottoms.get(cup) : 0;
        if (!cup.canBeRemoved(contactY)) {
            showError("La taza jerarquica se ha fijado en el suelo y rechaza ser eliminada.");
            lastOk = false;
            return;
        }

        if (cup.hasCover()) {
            Lid lid = cup.removeLid();
            lid.makeInvisible();
            items.remove(lid);
        }
        cup.makeInvisible();
        items.remove(cup);
        currentHeight = calculateHeight();
        redrawTower();
        lastOk = true;
    }

    /**
     * Agrega una tapa a la torre.
     * 
     * @param i numero de la tapa a agregar
     */
    public void pushLid(int i) {
        pushLid(i, "normal");
    }

    /**
     * Agrega una tapa polimorfica a la torre.
     * 
     * @param i    numero de la tapa a agregar
     * @param type tipo de la tapa: "normal", "fearful", "crazy", "evil"
     */
    public void pushLid(int i, String type) {
        if (2 * i > this.width) {
            showError("La tapa " + i + " no cabe: su ancho (" + (2 * i) + " cm) excede el ancho de la torre ("
                    + this.width + " cm).");
            lastOk = false;
            return;
        }

        if (lidExists(i)) {
            showError("Ya existe una tapa con el numero " + i + " en la torre.");
            lastOk = false;
            return;
        }

        // Logica FearfulLid: solo entra si su taza esta

        if (type.equalsIgnoreCase("fearful") && !cupExists(i)) {
            showError("La Fearful Lid " + i + " tiene miedo de entrar porque su taza duena no esta en la torre.");
            lastOk = false;
            return;
        }

        Lid newLid;
        if (type.equalsIgnoreCase("fearful")) {
            newLid = new FearfulLid(i, pixelsPerCm);
        } else if (type.equalsIgnoreCase("crazy")) {
            newLid = new CrazyLid(i, pixelsPerCm);
        } else if (type.equalsIgnoreCase("evil")) {
            newLid = new EvilLid(i, pixelsPerCm);
        } else {
            newLid = new Lid(i, pixelsPerCm);
        }

        items.add(newLid);

        int newHeight = calculateHeight();
        if (newHeight > maxHeight) {
            items.remove(items.size() - 1); // remove the newly added lid
            showError("La tapa " + i + " no cabe: excede la altura maxima de " + maxHeight + " cm.");
            lastOk = false;
            return;
        }
        currentHeight = newHeight;
        redrawTower();
        // Hook polimorfico: la tapa puede reaccionar despues de ser colocada

        newLid.onAfterPlaced(items);
        if (!items.contains(newLid)) {
            // EvilLid (u otra) se elimino a si misma; recalcular y redibujar

            currentHeight = calculateHeight();
            redrawTower();
        }
        lastOk = true;
    }

    /**
     * Elimina la ultima tapa agregada a la torre.
     */
    public void popLid() {
        Lid lastLid = getLastLid();
        if (lastLid == null) {
            showError("No hay tapas en la torre para eliminar.");
            lastOk = false;
            return;
        }
        Cup cup = findCup(lastLid.getNumber());

        // Logica polimorfica: no se deja quitar si cumple sus propias reglas

        boolean isCovering = (cup != null && cup.getLid() == lastLid);
        if (!lastLid.canBeRemoved(isCovering)) {
            showError("La tapa se niega a ser eliminada.");
            lastOk = false;
            return;
        }

        if (cup != null && cup.getLid() == lastLid) {
            cup.removeLid();
        }
        lastLid.makeInvisible();
        items.remove(lastLid);
        currentHeight = calculateHeight();
        redrawTower();
        lastOk = true;
    }

    /**
     * Elimina una tapa especifica de la torre por su numero.
     * 
     * @param i numero de la tapa a eliminar
     */
    public void removeLid(int i) {
        Lid lid = findLid(i);
        if (lid == null) {
            lastOk = false;
            return;
        }
        Cup cup = findCup(i);

        // Logica polimorfica: no se deja quitar si cumple sus propias reglas

        boolean isCovering = (cup != null && cup.getLid() == lid);
        if (!lid.canBeRemoved(isCovering)) {
            showError("La tapa se niega a ser eliminada.");
            lastOk = false;
            return;
        }

        if (cup != null && cup.getLid() == lid) {
            cup.removeLid();
        }
        lid.makeInvisible();
        items.remove(lid);
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
     * Retorna los numeros de las tazas que tienen tapa,
     * ordenados de menor a mayor.
     * 
     * @return arreglo con los numeros de las tazas tapadas
     */
    public int[] lidedCups() {
        ArrayList<Integer> lidedList = new ArrayList<Integer>();
        for (StackableItem item : items) {
            if (item.isCup()) {
                Cup cup = (Cup) item;
                if (cup.hasCover()) {
                    lidedList.add(cup.getNumber());
                }
            }
        }
        // No retornamos este ArrayList porque en los requisitos de diseno pide

        // retornar

        // int[]

        int[] result = new int[lidedList.size()];
        for (int i = 0; i < lidedList.size(); i++) {
            result[i] = lidedList.get(i);
        }
        return result;
    }

    /**
     * Retorna los elementos apilados de base a cima.
     * Cada elemento es un arreglo de dos strings: tipo y numero.
     * Ejemplo: {{"cup","4"},{"lid","4"}}
     * 
     * @return arreglo bidimensional con tipo y numero de cada elemento
     */
    public String[][] stackingItems() {
        String[][] result = new String[items.size()][2];
        for (int i = 0; i < items.size(); i++) {
            StackableItem item = items.get(i);
            if (item.isCup()) {
                result[i][0] = "cup";
            } else {
                result[i][0] = "lid";
            }
            result[i][1] = String.valueOf(item.getNumber());
        }
        return result;
    }

    /**
     * Ordena los elementos de la torre de mayor a menor.
     * Solo incluye los elementos que quepan en la altura maxima.
     * El numero menor queda en la cima. Si una taza tiene tapa,
     * la tapa se coloca sobre la taza.
     */
    public void orderTower() {
        ArrayList<Cup> sortedCups = new ArrayList<>();
        ArrayList<Lid> pairedLids = new ArrayList<>();
        ArrayList<Lid> soloLids = new ArrayList<>();

        for (StackableItem item : items) {
            item.makeInvisible();
            if (item.isCup()) {
                sortedCups.add((Cup) item);
            } else if (item.isLid()) {
                Lid lid = (Lid) item;
                // Identifica si esta tapa tiene una taza correspondiente en la torre

                boolean hasCup = false;
                for (StackableItem cItem : items) {
                    if (cItem.isCup() && cItem.getNumber() == lid.getNumber()) {
                        hasCup = true;
                        break;
                    }
                }
                if (hasCup) {
                    pairedLids.add(lid);
                } else {
                    soloLids.add(lid);
                }
            }
        }

        // Ordena las tazas de forma descendente (el numero mAs grande abajo)

        sortedCups.sort((a, b) -> Integer.compare(b.getNumber(), a.getNumber()));

        items.clear();
        currentHeight = 0;

        // Bucle 1: Agrega todas las tazas secuencialmente para permitir la anidacion

        // fAsica.

        for (Cup c : sortedCups) {
            Cup newCup = (Cup) c.createCopy();
            items.add(newCup);
            if (calculateHeight() <= maxHeight) {
                currentHeight = calculateHeight();
            } else {
                items.remove(newCup);
            }
        }

        // Bucle 2: Agrega las tapas respectivas de las tazas ordenadas al final.

        // DEBEMOS tapar desde la taza mas pequena a la mas grande. Si tapamos la mas

        // grande primero,

        // actuaria como un techo solido y bloquearia que las tapas mas pequenas se

        // aniden en el interior.

        ArrayList<Cup> cupsToCap = new ArrayList<>(sortedCups);
        cupsToCap.sort((a, b) -> Integer.compare(a.getNumber(), b.getNumber()));

        for (Cup c : cupsToCap) {
            Lid matchingLid = null;
            for (Lid l : pairedLids) {
                if (l.getNumber() == c.getNumber()) {
                    matchingLid = l;
                    break;
                }
            }
            if (matchingLid != null) {
                // Confirma que la taza realmente entro en la lista de elementos antes de

                // taparla

                boolean cupExists = false;
                for (StackableItem item : items) {
                    if (item.isCup() && item.getNumber() == c.getNumber()) {
                        cupExists = true;
                        break;
                    }
                }

                if (cupExists) {
                    Lid newLid = (Lid) matchingLid.createCopy();
                    items.add(newLid);
                    if (calculateHeight() <= maxHeight) {
                        currentHeight = calculateHeight();
                    } else {
                        items.remove(newLid);
                    }
                }
            }
        }

        for (Lid l : soloLids) {
            Lid newLid = (Lid) l.createCopy();
            items.add(newLid);
            if (calculateHeight() <= maxHeight) {
                currentHeight = calculateHeight();
            } else {
                items.remove(newLid);
            }
        }

        redrawTower();
        lastOk = true;
    }

    /**
     * Invierte el orden de los elementos de la torre.
     * Solo incluye los elementos que quepan en la altura maxima.
     */
    public void reverseTower() {
        ArrayList<ArrayList<StackableItem>> blocks = new ArrayList<>();
        ArrayList<Lid> assignedLids = new ArrayList<>();

        // Agrupar elementos. Taza + su Tapa (incluso si cayo mucho despues) = 1 bloque.

        for (int i = 0; i < items.size(); i++) {
            StackableItem item = items.get(i);

            if (item.isCup() && !assignedLids.contains(item)) { // check sanity
                ArrayList<StackableItem> block = new ArrayList<>();
                block.add(item);

                // Buscar si existe su tapa arrojada despues en la lista

                for (int j = i + 1; j < items.size(); j++) {
                    StackableItem searchLid = items.get(j);
                    if (searchLid.isLid() && searchLid.getNumber() == item.getNumber()) {
                        block.add(searchLid);
                        assignedLids.add((Lid) searchLid);
                        break; // encontramos su par, forma el bloque
                    }
                }
                blocks.add(block);
            } else if (item.isLid()) {
                if (!assignedLids.contains(item)) {
                    // Es una tapa sin fondo o q no tenia taza previa (soloLid)

                    ArrayList<StackableItem> block = new ArrayList<>();
                    block.add(item);
                    blocks.add(block);
                }
            }
        }

        // Inversion completa y literal del arreglo de bloques

        java.util.Collections.reverse(blocks);

        ArrayList<StackableItem> backupItems = new ArrayList<>(items);
        items.clear();
        currentHeight = 0;

        // Reinsertamos respetando la nueva inversion

        for (ArrayList<StackableItem> block : blocks) {
            for (StackableItem originalItem : block) {
                StackableItem newItem = originalItem.createCopy();

                items.add(newItem);

                if (calculateHeight() <= maxHeight) {
                    currentHeight = calculateHeight();
                } else {
                    items.remove(items.size() - 1);
                }
            }
        }

        for (StackableItem oldItem : backupItems) {
            oldItem.makeInvisible();
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
        for (StackableItem item : items) {
            item.makeInvisible();
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

        int num1 = Integer.parseInt(o1[1]);
        int num2 = Integer.parseInt(o2[1]);

        int idx1 = -1;
        int idx2 = -1;

        for (int i = 0; i < items.size(); i++) {
            StackableItem item = items.get(i);
            if (o1[0].equals("cup") && item.isCup() && item.getNumber() == num1) {
                idx1 = i;
            } else if (o1[0].equals("lid") && item.isLid() && item.getNumber() == num1) {
                idx1 = i;
            }
            if (o2[0].equals("cup") && item.isCup() && item.getNumber() == num2) {
                idx2 = i;
            } else if (o2[0].equals("lid") && item.isLid() && item.getNumber() == num2) {
                idx2 = i;
            }
        }

        if (idx1 == -1 || idx2 == -1) {
            showError("Uno o ambos elementos no existen en la torre.");
            lastOk = false;
            return;
        }

        java.util.Collections.swap(items, idx1, idx2);

        if (!isValidConfiguration()) {
            java.util.Collections.swap(items, idx1, idx2);
            showError(
                    "El intercambio no es posible: provoca posiciones invAƒÂ¡lidas donde las piezas no caben y se sobreponen.");
            lastOk = false;
            return;
        }

        int newHeight = calculateHeight();
        if (newHeight > maxHeight) {
            java.util.Collections.swap(items, idx1, idx2);
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
        ArrayList<StackableItem> newOrder = new ArrayList<>();
        ArrayList<Lid> strayLids = new ArrayList<>();

        for (StackableItem item : items) {
            if (item.isLid()) {
                Lid lid = (Lid) item;
                if (findCup(lid.getNumber()) != null) {
                    strayLids.add(lid);
                }
            }
        }

        for (StackableItem item : items) {
            if (item.isCup()) {
                newOrder.add(item);
                Cup cup = (Cup) item;
                // Busca la tapa huerfana correspondiente

                for (Lid lid : strayLids) {
                    if (lid.getNumber() == cup.getNumber()) {
                        newOrder.add(lid);
                        break;
                    }
                }
            } else if (item.isLid()) {
                Lid lid = (Lid) item;
                if (!strayLids.contains(lid)) {
                    newOrder.add(lid);
                }
            }
        }

        ArrayList<StackableItem> backupOrder = new ArrayList<>(items);
        items.clear();
        items.addAll(newOrder);

        int newHeight = calculateHeight();
        if (newHeight > maxHeight) {
            items.clear();
            items.addAll(backupOrder);
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

        for (int i = 0; i < items.size(); i++) {
            for (int j = i + 1; j < items.size(); j++) {
                java.util.Collections.swap(items, i, j);
                int testH = calculateHeight();
                boolean valid = isValidConfiguration();
                java.util.Collections.swap(items, i, j);

                if (valid && testH <= maxHeight && testH < actualH) {
                    String[][] best = new String[2][2];
                    StackableItem s1 = items.get(i);
                    StackableItem s2 = items.get(j);
                    best[0][0] = s1.isCup() ? "cup" : "lid";
                    best[0][1] = String.valueOf(s1.getNumber());
                    best[1][0] = s2.isCup() ? "cup" : "lid";
                    best[1][1] = String.valueOf(s2.getNumber());
                    lastOk = true;
                    return best;
                }
            }
        }
        lastOk = true;
        return null;
    }

    /**
     * Indica si la ultima operaciAn se realizA con exito.
     * 
     * @return true si la ultima operaciAn fue exitosa
     */
    public boolean ok() {
        return lastOk;
    }

    /**
     * Revisa si el arreglo actual de items provoca que items mas grandes se
     * sobrepongan sobre items mas pequenos, lo cual es considerado un estado "que
     * no cabe".
     * 
     * @return true si la configuracion es fisicamente valida sin sobreposiciones
     *         ilegales
     */
    private boolean isValidConfiguration() {
        java.util.HashMap<StackableItem, Integer> bottoms = new java.util.HashMap<>();
        for (StackableItem item : items) {
            int number = item.getNumber();
            int contactY = 0;
            StackableItem support = null;

            for (StackableItem placedItem : bottoms.keySet()) {
                int placedY = bottoms.get(placedItem);
                int testY = 0;

                if (item.isCup()) {
                    if (placedItem.isCup()) {
                        int pNumber = placedItem.getNumber();
                        if (number >= pNumber) {
                            testY = placedY + 2 * pNumber - 1;
                        } else {
                            testY = placedY + 1;
                        }
                    } else { // lid
                        testY = placedY + 1;
                    }
                } else { // lid
                    if (placedItem.isLid()) {
                        testY = placedY + 1;
                    } else if (placedItem.isCup()) {
                        int pNumber = placedItem.getNumber();
                        if (pNumber == number) {
                            testY = placedY + 2 * pNumber - 1;
                        } else if (number > pNumber) {
                            testY = placedY + 2 * pNumber - 1;
                        } else {
                            testY = placedY + 1;
                        }
                    }
                }

                if (testY > contactY) {
                    contactY = testY;
                    support = placedItem;
                }
            }

            // Si el item esta flotando apoyado en un item mas pequeno, es invalido

            if (support != null && number > support.getNumber()) {
                return false;
            }
            bottoms.put(item, contactY);
        }
        return true;
    }

    // ---------- metodos privados auxiliares ----------

    /**
     * Busca una taza por su numero.
     * 
     * @param number numero de la taza a buscar
     * @return la taza encontrada, o null si no existe
     */
    private Cup findCup(int number) {
        for (StackableItem item : items) {
            if (item.isCup() && item.getNumber() == number) {
                return (Cup) item;
            }
        }
        return null;
    }

    /**
     * Busca una tapa por su numero.
     * 
     * @param number numero de la tapa a buscar
     * @return la tapa encontrada, o null si no existe
     */
    private Lid findLid(int number) {
        for (StackableItem item : items) {
            if (item.isLid() && item.getNumber() == number) {
                return (Lid) item;
            }
        }
        return null;
    }

    /**
     * Verifica si ya existe una taza con ese numero en la torre.
     * 
     * @param number numero a verificar
     * @return true si ya existe
     */
    private boolean cupExists(int number) {
        return findCup(number) != null;
    }

    /**
     * Verifica si ya existe una tapa con ese numero en la torre.
     * 
     * @param number numero a verificar
     * @return true si ya existe
     */
    private boolean lidExists(int number) {
        return findLid(number) != null;
    }

    /**
     * Retorna la ultima taza agregada a la torre.
     * 
     * @return la ultima taza, o null si no hay tazas
     */
    private Cup getLastCup() {
        for (int i = items.size() - 1; i >= 0; i--) {
            StackableItem item = items.get(i);
            if (item.isCup()) {
                return (Cup) item;
            }
        }
        return null;
    }

    /**
     * Retorna la ultima tapa agregada a la torre.
     * 
     * @return la ultima tapa, o null si no hay tapas
     */
    private Lid getLastLid() {
        for (int i = items.size() - 1; i >= 0; i--) {
            StackableItem item = items.get(i);
            if (item.isLid()) {
                return (Lid) item;
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

    java.util.HashMap<StackableItem, Integer> calculatePositions() {
        for (StackableItem item : items) {
            if (item.isCup()) {
                ((Cup) item).removeLid();
            }
        }

        java.util.HashMap<StackableItem, Integer> bottoms = new java.util.HashMap<>();
        for (StackableItem item : items) {
            if (item.isCup()) {
                Cup cup = (Cup) item;
                int number = cup.getNumber();
                int contactY = 0;
                for (StackableItem placedItem : bottoms.keySet()) {
                    int placedY = bottoms.get(placedItem);
                    if (placedItem.isCup()) {
                        int pNumber = placedItem.getNumber();
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
                bottoms.put(item, contactY);
            } else { // lid
                Lid lid = (Lid) item;
                int number = lid.getNumber();

                int contactY = 0;
                StackableItem restingOn = null;

                // Calcula la fisica de colision para una Tapa en caida libre.

                // Una Tapa solo deja de caer y aterriza si choca contra:

                // 1) Otra Tapa (techo solido cerrado).

                // 2) El borde superior de su propia Taza duena exacta.

                // 3) El suelo interior / borde de cualquier otra taza en la que caiga.

                for (StackableItem placedItem : bottoms.keySet()) {
                    int placedY = bottoms.get(placedItem);
                    int testY = 0;

                    if (placedItem.isLid()) {
                        testY = placedY + 1; // Techo solido, descansamos encima de el.
                    } else if (placedItem.isCup()) {
                        int pNumber = placedItem.getNumber();
                        if (pNumber == number) {
                            if (lid.isCrazyLid()) {
                                testY = placedY;
                                for (StackableItem k : bottoms.keySet()) {
                                    if (bottoms.get(k) >= placedY && k != lid) {
                                        bottoms.put(k, bottoms.get(k) + 1);
                                    }
                                }
                            } else {
                                testY = placedY + 2 * pNumber - 1;
                            }
                        } else if (number > pNumber) {
                            // Somos una tapa mas grande cayendo sobre una taza mas pequena.

                            // Descansamos sobre el borde de la taza pequena (actuando como un pilar).

                            testY = placedY + 2 * pNumber - 1;
                        } else {
                            // Somos una tapa mas pequena cayendo DENTRO de una taza vacia mas grande.

                            // Atravesamos sus paredes huecas y descansamos en su suelo interior profundo.

                            testY = placedY + 1;
                        }
                    }

                    if (testY > contactY) {
                        contactY = testY;
                        restingOn = placedItem;
                    } else if (testY == contactY) {
                        if (placedItem.isCup() && placedItem.getNumber() == number) {
                            // Desempate de prioridad: Siempre prefiere anclarse a nuestra taza dueAa

                            // exacta

                            // por encima de otros suelos al azar posicionados en la misma altura Y.

                            restingOn = placedItem;
                        } else if (restingOn == null) {
                            restingOn = placedItem;
                        }
                    }
                }

                bottoms.put(item, contactY);

                if (restingOn != null && restingOn.isCup() && restingOn.getNumber() == number) {
                    Cup cup = (Cup) restingOn;
                    cup.setLid(lid);
                    if (isVisible)
                        cup.makeVisible();
                }
            }
        }
        return bottoms;
    }

    /**
     * Retorna el elemento sobre el que descansa fisicamente una taza,
     * o null si descansa en el suelo. Usa la misma logica de colision
     * de calculatePositions para una sola taza.
     */
    StackableItem getRestingOn(StackableItem target) {
        java.util.HashMap<StackableItem, Integer> positions = calculatePositions();
        if (!positions.containsKey(target)) return null;
        int targetY = positions.get(target);
        if (targetY == 0) return null;

        int number = target.getNumber();
        StackableItem resting = null;
        int contactY = 0;

        for (StackableItem placedItem : positions.keySet()) {
            if (placedItem == target) continue;
            int placedY = positions.get(placedItem);
            int testY = 0;

            if (placedItem.isLid()) {
                testY = placedY + 1;
            } else if (placedItem.isCup()) {
                int pNumber = placedItem.getNumber();
                if (number >= pNumber) {
                    testY = placedY + 2 * pNumber - 1;
                } else {
                    testY = placedY + 1;
                }
            }

            if (testY > contactY) {
                contactY = testY;
                resting = placedItem;
            } else if (testY == contactY && resting == null) {
                resting = placedItem;
            }
        }
        return (contactY == targetY) ? resting : null;
    }

    /**
     * Recalcula y dibuja la posicion de todos los elementos en el canvas.
     */
    private void redrawTower() {
        int canvasCenter = 150;
        int groundY = 280;

        java.util.HashMap<StackableItem, Integer> bottoms = calculatePositions();

        for (StackableItem item : items) {
            int simY = bottoms.get(item);
            if (item.isCup()) {
                Cup cup = (Cup) item;
                int number = cup.getNumber();

                int cupWidth = number * pixelsPerCm * 2;
                int xPos = canvasCenter - cupWidth / 2;
                int cupHeightCm = cup.getHeight();
                int topSimY = simY + cupHeightCm;

                int yPos = groundY - (topSimY * pixelsPerCm);

                cup.setPosition(xPos, yPos);
                if (isVisible)
                    cup.makeVisible();
            } else if (item.isLid()) {
                Lid lid = (Lid) item;
                int number = lid.getNumber();

                int lidWidth = number * pixelsPerCm * 2;
                int xPos = canvasCenter - lidWidth / 2;
                int topSimY = simY + lid.getHeight();

                int yPos = groundY - (topSimY * pixelsPerCm);

                lid.setPosition(xPos, yPos);
                if (isVisible)
                    lid.makeVisible();
            }
        }

        // Dibuja los bordes de el simulador y las marcas de altura

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
        java.util.HashMap<StackableItem, Integer> bottoms = calculatePositions();
        for (StackableItem item : items) {
            int y = bottoms.get(item);
            maxH = Math.max(maxH, y + item.getHeight());
        }
        return maxH;
    }
}
