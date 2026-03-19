import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad para la clase TowerContest.
 * Verifica que el solucionador del Problema J (ICPC 2025) responda
 * correctamente ante distintos escenarios: casos oficiales, limites
 * de rango, y alturas imposibles.
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
        // Ejemplo oficial del problema: n=4, h=9.
        // Debe devolver una permutacion valida cuya torre mida exactamente 9.
        String result = contest.solve(4, 9);
        assertTrue("El ejemplo 1 oficial debe tener solucion", isValid(result, 4, 9));
    }

    @Test
    public void accordingASShouldReturnImpossibleForSample2() {
        // Ejemplo oficial: n=4, h=100. Imposible porque el maximo es 16.
        assertEquals("impossible", contest.solve(4, 100));
    }

    @Test
    public void accordingASShouldSolveSingleCup() {
        // Con una sola taza, la unica altura posible es 1.
        String result = contest.solve(1, 1);
        assertTrue("Una sola taza siempre da altura 1", isValid(result, 1, 1));
    }

    @Test
    public void accordingASShouldSolveMinimumHeight() {
        // La altura minima para 4 tazas es 7 (todas anidadas en la mayor).
        String result = contest.solve(4, 7);
        assertTrue("La altura minima para n=4 es 7", isValid(result, 4, 7));
    }

    @Test
    public void accordingASShouldSolveMaximumHeight() {
        // La altura maxima para 4 tazas es 16 (todas apiladas de menor a mayor).
        String result = contest.solve(4, 16);
        assertTrue("La altura maxima para n=4 es 16", isValid(result, 4, 16));
    }

    @Test
    public void accordingASShouldSolveIntermediateHeight() {
        // h=10 esta dentro del rango valido [7, 16] para n=4.
        String result = contest.solve(4, 10);
        assertTrue("h=10 es alcanzable para n=4", isValid(result, 4, 10));
    }

    // --- Que NO deberia hacer ---

    @Test
    public void accordingASShouldNotSolveBelowMinimum() {
        // h=6 esta por debajo del minimo 7 para n=4. Debe ser imposible.
        assertEquals("impossible", contest.solve(4, 6));
    }

    @Test
    public void accordingASShouldNotSolveAboveMaximum() {
        // h=17 esta por encima del maximo 16 para n=4. Debe ser imposible.
        assertEquals("impossible", contest.solve(4, 17));
    }

    @Test
    public void accordingASShouldNotSolveUnreachableSum() {
        // h=2 no se puede formar con ninguna combinacion de tazas.
        assertEquals("impossible", contest.solve(4, 2));
    }

    // --- Metodos auxiliares de verificacion ---

    /**
     * Verifica que el resultado sea una permutacion valida de las n tazas
     * y que al simular la fisica del apilamiento se obtenga la altura h.
     * 
     * @param result respuesta del solucionador
     * @param n      cantidad de tazas
     * @param h      altura esperada
     * @return true si la respuesta es correcta
     */
    private boolean isValid(String result, int n, long h) {
        if (result == null || result.equals("impossible"))
            return false;
        String[] parts = result.trim().split("\\s+");
        if (parts.length != n)
            return false;

        // Verifica que cada valor sea una altura impar valida y no se repita
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

        // Simula la fisica y compara la altura resultante
        return simulateHeight(parts) == h;
    }

    /**
     * Simula la fisica del apilamiento de tazas y calcula la altura total.
     * Cada taza se apoya en el punto mas alto que la soporte:
     * - Si es mas grande o igual que otra, se apoya en su borde superior.
     * - Si es mas pequeña, cae adentro y se apoya en el piso interno.
     * 
     * @param seq arreglo con las alturas de las tazas en orden de colocacion
     * @return altura total de la torre resultante
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
