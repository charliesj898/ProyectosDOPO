import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Solucionador del Problema J - Stacking Cups (ICPC 2025).
 * 
 * Algoritmo recursivo de dos casos:
 * Caso A: pre-stack ascendente + cup n encima -> h = sum(pre-stack) + (2n-1)
 * Caso B: cup n como contenedor primero, resolver recursivamente n-1 cups para h-1
 * 
 * @author Carlos Sanchez, Samuel Argalle
 * @version 3.0
 */
public class TowerContest {

    /**
     * Sobrecarga de conveniencia para llamar con int.
     */
    public String solve(int n, int h) {
        return solve(n, (long) h);
    }

    /**
     * Resuelve el problema: encuentra un orden de n tazas que produzca altura h.
     * 
     * @param n cantidad de tazas (alturas 1, 3, ..., 2n-1)
     * @param h altura objetivo
     * @return alturas separadas por espacio, o "impossible"
     */
    public String solve(int n, long h) {
        long minH = 2L * n - 1;
        long maxH = (long) n * n;
        if (h < minH || h > maxH) {
            return "impossible";
        }

        // Caso A: pre-stack S ascendente, luego cup n, luego resto descendente
        // h = sum_heights(S) + (2n-1), donde sum_heights(S) = delta
        long delta = h - minH;
        if (canFormSum(n - 1, delta)) {
            List<Integer> preStack = buildSubset(n - 1, delta);
            if (preStack != null) {
                return buildCaseA(n, preStack);
            }
        }

        // Caso B: cup n primero (contenedor), resolver n-1 cups para h-1
        if (n > 1 && h - 1 >= 2L * (n - 1) - 1 && h - 1 <= (long) (n - 1) * (n - 1)) {
            String sub = solve(n - 1, h - 1);
            if (!sub.equals("impossible")) {
                return (2 * n - 1) + " " + sub;
            }
        }

        return "impossible";
    }

    /**
     * Sobrecarga de conveniencia para simulate con int.
     */
    public void simulate(int n, int h) {
        simulate(n, (long) h);
    }

    /**
     * Simula graficamente la solucion usando la clase Tower.
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
                            + "\n(N muy grande para el canvas grafico.)",
                    "Solucion", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] parts = answer.split(" ");
        final Tower simTower = new Tower(n * 3 + 20, (int) ((long) n * n) + 10);
        simTower.makeVisible();
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
     * Verifica si target es alcanzable como subset sum de {1,3,...,2m-1}.
     * Los unicos huecos son 2 y m*m-2 (para m >= 2).
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
        if (target == 2 || target == maxSum - 2)
            return false;
        return true;
    }

    /**
     * Construye greedily un subset de cups {1..m} cuya suma de alturas = target.
     * Retorna lista de numeros de cup en el subset (ordenada ascendente).
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
     * Construye la secuencia de salida para Caso A:
     * pre-stack ascendente + cup n + resto descendente.
     */
    private String buildCaseA(int n, List<Integer> preStack) {
        StringBuilder sb = new StringBuilder();
        // Pre-stack en orden ascendente (alturas)
        for (int cup : preStack) {
            sb.append(2 * cup - 1).append(" ");
        }
        // Cup n
        sb.append(2 * n - 1).append(" ");
        // Resto en orden descendente (nested)
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
