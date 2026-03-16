import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad para TowerContest — Problema J ICPC 2025.
 * Siguiendo la premisa: "que deberia y que no deberia hacer".
 * 
 * @author Carlos Sanchez, Samuel Argalle
 * @version 3.0
 */
public class TowerContestTest {

    private TowerContest contest;

    @Before
    public void setUp() {
        contest = new TowerContest();
    }

    // --- Que SI deberia hacer ---

    @Test
    public void accordingASShouldSolveSample1() {
        // Sample oficial: n=4, h=9 -> debe devolver una permutacion valida
        String result = contest.solve(4, 9);
        assertTrue("Sample 1 debe ser posible", isValid(result, 4, 9));
    }

    @Test
    public void accordingASShouldReturnImpossibleForSample2() {
        // Sample oficial: n=4, h=100 -> imposible (encima del maximo 16)
        assertEquals("impossible", contest.solve(4, 100));
    }

    @Test
    public void accordingASShouldSolveSingleCup() {
        // n=1: la unica taza tiene altura 1, la unica solucion es "1"
        String result = contest.solve(1, 1);
        assertTrue("Una sola taza con h=1 debe resolverse", isValid(result, 1, 1));
    }

    @Test
    public void accordingASShouldSolveMinimumHeight() {
        // Altura minima para n=4 es 2*4-1 = 7 (todas anidadas dentro de la mayor)
        String result = contest.solve(4, 7);
        assertTrue("Altura minima n=4 (h=7) debe resolverse", isValid(result, 4, 7));
    }

    @Test
    public void accordingASShouldSolveMaximumHeight() {
        // Altura maxima para n=4 es 4^2 = 16 (todas apiladas una sobre otra)
        String result = contest.solve(4, 16);
        assertTrue("Altura maxima n=4 (h=16) debe resolverse", isValid(result, 4, 16));
    }

    @Test
    public void accordingASShouldSolveIntermediateHeight() {
        // h=10 esta dentro del rango [7,16] para n=4
        String result = contest.solve(4, 10);
        assertTrue("Altura intermedia n=4 (h=10) debe resolverse", isValid(result, 4, 10));
    }

    // --- Que NO deberia hacer ---

    @Test
    public void accordingASShouldNotSolveBelowMinimum() {
        // n=4: h=6 es menor que el minimo (7), debe ser imposible
        assertEquals("impossible", contest.solve(4, 6));
    }

    @Test
    public void accordingASShouldNotSolveAboveMaximum() {
        // n=4: h=17 es mayor que el maximo (16), debe ser imposible
        assertEquals("impossible", contest.solve(4, 17));
    }

    @Test
    public void accordingASShouldNotSolveUnreachableSum() {
        // h=2 no puede formarse como suma de ningun subset de {1,3,5,7}
        assertEquals("impossible", contest.solve(4, 2));
    }

    // --- Utilidades de verificacion ---

    /**
     * Verifica que result sea una permutacion valida de las n tazas
     * y que la fisica del apilamiento produzca exactamente h.
     */
    private boolean isValid(String result, int n, long h) {
        if (result == null || result.equals("impossible"))
            return false;
        String[] parts = result.trim().split("\\s+");
        if (parts.length != n)
            return false;

        boolean[] seen = new boolean[n + 1];
        for (String p : parts) {
            int height;
            try {
                height = Integer.parseInt(p);
            } catch (NumberFormatException e) {
                return false;
            }
            if (height < 1 || height > 2 * n - 1 || height % 2 == 0)
                return false;
            int cup = (height + 1) / 2;
            if (cup < 1 || cup > n || seen[cup])
                return false;
            seen[cup] = true;
        }
        return simulateHeight(parts) == h;
    }

    /**
     * Simula la fisica de apilamiento y retorna la altura total.
     */
    private long simulateHeight(String[] seq) {
        long[] tops = new long[seq.length];
        for (int i = 0; i < seq.length; i++) {
            int hi = Integer.parseInt(seq[i]);
            long contactY = 0;
            for (int j = 0; j < i; j++) {
                int hj = Integer.parseInt(seq[j]);
                long base = tops[j] - hj;
                contactY = Math.max(contactY, hi >= hj ? tops[j] : base + 1);
            }
            tops[i] = contactY + hi;
        }
        long max = 0;
        for (long t : tops)
            max = Math.max(max, t);
        return max;
    }
}
