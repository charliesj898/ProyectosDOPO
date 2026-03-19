import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resuelve el Problema J (Stacking Cups) de la maraton ICPC 2025.
 * 
 * Dadas n tazas con alturas 1, 3, 5, ..., (2n-1), encuentra en que
 * orden apilarlas para lograr exactamente la altura h.
 * 
 * El algoritmo usa dos estrategias (casos) de forma recursiva:
 * Caso A: coloca algunas tazas en orden ascendente, luego la taza n
 * encima, y el resto queda anidado.
 * Caso B: coloca la taza n como contenedor (primero), y resuelve
 * recursivamente para n-1 tazas con altura h-1.
 * 
 * @author Carlos Sanchez, Samuel Argalle
 * @version 3.0
 */
public class TowerContest {

    // Guarda la torre de la ultima simulacion para poder limpiarla
    private Tower lastTower;

    /**
     * Resuelve el problema para n tazas y altura objetivo h.
     * Permite recibir h como int por conveniencia desde BlueJ.
     * 
     * @param n cantidad de tazas
     * @param h altura objetivo
     * @return las alturas de cada taza en orden, o "impossible"
     */
    public String solve(int n, int h) {
        return solve(n, (long) h);
    }

    /**
     * Resuelve el problema para n tazas y altura objetivo h (como long).
     * Se usa long porque en el problema original h puede llegar hasta 4x10^10.
     * 
     * @param n cantidad de tazas
     * @param h altura objetivo
     * @return las alturas de cada taza separadas por espacio, o "impossible"
     */
    public String solve(int n, long h) {
        long minH = 2L * n - 1;
        long maxH = (long) n * n;

        // Fuera del rango posible: imposible
        if (h < minH || h > maxH) {
            return "impossible";
        }

        // Caso A: poner algunas tazas ascendentes antes de la taza n
        // La diferencia (delta) entre h y la altura minima es lo que
        // debe sumar el grupo ascendente
        long delta = h - minH;
        if (canFormSum(n - 1, delta)) {
            List<Integer> preStack = buildSubset(n - 1, delta);
            if (preStack != null) {
                return buildCaseA(n, preStack);
            }
        }

        // Caso B: la taza n va primero como contenedor.
        // Las n-1 tazas restantes se acomodan adentro para
        // lograr una sub-torre de altura h-1 (porque el piso
        // de la taza n tiene 1 cm de grosor).
        if (n > 1 && h - 1 >= 2L * (n - 1) - 1 && h - 1 <= (long) (n - 1) * (n - 1)) {
            String sub = solve(n - 1, h - 1);
            if (!sub.equals("impossible")) {
                return (2 * n - 1) + " " + sub;
            }
        }

        return "impossible";
    }

    /**
     * Simula graficamente la solucion dibujandola en el canvas.
     * Permite recibir h como int por conveniencia desde BlueJ.
     * 
     * @param n cantidad de tazas
     * @param h altura objetivo
     */
    public void simulate(int n, int h) {
        simulate(n, (long) h);
    }

    /**
     * Simula graficamente la solucion dibujandola en el canvas.
     * Si la solucion existe, crea una torre y va agregando las tazas
     * una por una con una pausa animada.
     * 
     * @param n cantidad de tazas
     * @param h altura objetivo
     */
    public void simulate(int n, long h) {
        String answer = solve(n, h);

        if (answer.equals("impossible")) {
            JOptionPane.showMessageDialog(null,
                    "La altura " + h + " es imposible para " + n + " tazas.\n"
                            + "Rango valido: [" + (2 * n - 1) + ", " + ((long) n * n) + "].",
                    "Imposible", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (n > 20) {
            JOptionPane.showMessageDialog(null,
                    "Solucion: " + answer.substring(0, Math.min(answer.length(), 200))
                            + (answer.length() > 200 ? "..." : "")
                            + "\n(N es muy grande para dibujarlo en el canvas.)",
                    "Solucion", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Limpiar la simulacion anterior si existe
        if (lastTower != null) {
            lastTower.makeInvisible();
        }

        String[] parts = answer.split(" ");
        final Tower simTower = new Tower(n * 3 + 20, (int) ((long) n * n) + 10);
        lastTower = simTower;
        simTower.makeVisible();

        // Hilo separado para animar la construccion sin congelar la interfaz
        new Thread(() -> {
            for (String str : parts) {
                int cupNum = (Integer.parseInt(str) + 1) / 2;
                simTower.pushCup(cupNum);
                try {
                    Thread.sleep(450);
                } catch (Exception ignored) {
                }
            }
            JOptionPane.showMessageDialog(null,
                    "Simulacion completada!\nAltura objetivo: " + h
                            + " cm\nAltura lograda: " + simTower.height() + " cm.",
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
        }).start();
    }

    // ---- Metodos privados ----

    /**
     * Verifica si es posible formar la suma indicada usando un subconjunto
     * de las alturas {1, 3, 5, ..., 2m-1}.
     * 
     * Las unicas sumas imposibles dentro del rango son exactamente 2
     * y m*m - 2 (para m mayor o igual a 2).
     * 
     * @param m      cantidad de tazas disponibles (1 a m)
     * @param target suma que se quiere alcanzar
     * @return true si la suma es alcanzable
     */
    private boolean canFormSum(int m, long target) {
        if (target == 0)
            return true;
        if (m <= 0)
            return false;
        long maxSum = (long) m * m;
        if (target < 0 || target > maxSum)
            return false;
        if (m == 1)
            return target == 1;
        // Los unicos dos valores imposibles dentro del rango
        if (target == 2 || target == maxSum - 2)
            return false;
        return true;
    }

    /**
     * Construye un subconjunto de tazas {1..m} cuya suma de alturas
     * sea exactamente target. Usa un enfoque greedy descendente:
     * intenta incluir la taza mas grande primero y verifica que el
     * resto siga siendo alcanzable.
     * 
     * @param m      cantidad de tazas disponibles
     * @param target suma objetivo
     * @return lista de numeros de taza del subconjunto (ascendente), o null
     */
    private List<Integer> buildSubset(int m, long target) {
        if (target == 0)
            return new ArrayList<>();
        if (m <= 0 || !canFormSum(m, target))
            return null;

        List<Integer> result = new ArrayList<>();
        long rem = target;
        for (int i = m; i >= 1; i--) {
            long val = 2L * i - 1;
            if (rem >= val && canFormSum(i - 1, rem - val)) {
                result.add(i);
                rem -= val;
                if (rem == 0)
                    break;
            }
        }
        if (rem != 0)
            return null;
        Collections.sort(result);
        return result;
    }

    /**
     * Arma la secuencia de salida para el Caso A.
     * Formato: tazas del pre-stack en orden ascendente de altura,
     * luego la taza n, luego las tazas restantes en orden descendente
     * (estas quedan anidadas dentro de la taza n).
     * 
     * @param n        total de tazas
     * @param preStack tazas que van antes de la taza n (ascendente)
     * @return las alturas en el orden correcto separadas por espacio
     */
    private String buildCaseA(int n, List<Integer> preStack) {
        StringBuilder sb = new StringBuilder();

        // Tazas del pre-stack en orden ascendente
        for (int cup : preStack) {
            sb.append(2 * cup - 1).append(" ");
        }

        // Taza n (la mas grande)
        sb.append(2 * n - 1).append(" ");

        // Tazas restantes en orden descendente (anidadas)
        boolean[] inPreStack = new boolean[n];
        for (int cup : preStack) {
            inPreStack[cup] = true;
        }
        for (int i = n - 1; i >= 1; i--) {
            if (!inPreStack[i]) {
                sb.append(2 * i - 1).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
