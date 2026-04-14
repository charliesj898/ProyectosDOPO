package tower;

import javax.swing.JOptionPane;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas de aceptacion del Simulador de Torre de Apilamiento.
 * Son pruebas visuales e interactivas: muestran el canvas, describen
 * lo ocurrido y piden confirmacion al usuario al final.
 *
 * @author Carlos Sanchez, Samuel Argalle
 * @version 4.0
 */
public class TowerATest {

    /**
     * Prueba 1: Simulador Polimorfico.
     * Demuestra visualmente FearfulLid (rechaza sin taza),
     * OpenerCup (destruye tapas) y EvilLid (elimina su par).
     */
    @Test
    public void acceptSimuladorPolimorfico() {
        Tower t = new Tower(40, 80);
        t.makeVisible();

        JOptionPane.showMessageDialog(null,
                "PRUEBA 1: Simulador Polimorfico\n\n"
                        + "Se demostraran 3 comportamientos especiales.\n"
                        + "Sigue las instrucciones de cada paso en el canvas.\n\n"
                        + "Presiona OK para iniciar.",
                "Aceptacion - Prueba 1", JOptionPane.INFORMATION_MESSAGE);

        // --- Escenario A: FearfulLid rechaza entrar sin su taza ---
        t.pushLid(2, "fearful"); // intento erroneo: taza 2 no existe
        assertFalse("FearfulLid no debe entrar sin su taza", t.ok());

        t.pushCup(2); // ahora si existe la taza
        t.pushLid(2, "fearful"); // debe entrar correctamente
        assertTrue("FearfulLid debe entrar con su taza presente", t.ok());

        // --- Escenario B: OpenerCup destruye solo la tapa que le bloquea el paso ---
        // Cup 2 y FearfulLid 2 ya estan. Agregamos cup 5 y lid 3 encima.
        t.pushCup(5);
        t.pushLid(3); // tapa 3 queda encima de FearfulLid 2; la OpenerCup 4 chocara con ella
        // OpenerCup 4: cae dentro de cup 5, colisiona fisicamente con lid 3 y la
        // destruye.
        // FearfulLid 2 queda mas abajo y no esta en el camino de colision.
        t.pushCup(4, "opener");
        assertTrue("insercion de OpenerCup debe ser exitosa", t.ok());

        // Lid 3 fue destruida (bloqueaba el paso)
        boolean lid3Existe = false;
        for (String[] item : t.stackingItems()) {
            if (item[0].equals("lid") && item[1].equals("3"))
                lid3Existe = true;
        }
        assertFalse("Lid 3 debe haberse destruido por colision", lid3Existe);

        // FearfulLid 2 NO fue destruida (estaba mas abajo, no en el camino de colision)
        boolean lid2Existe = false;
        for (String[] item : t.stackingItems()) {
            if (item[0].equals("lid") && item[1].equals("2"))
                lid2Existe = true;
        }
        assertTrue("FearfulLid 2 debe seguir en la torre", lid2Existe);

        // --- Escenario C: EvilLid elimina su taza al cubrirla ---
        t.pushCup(6); // cup 6, numero de uso exclusivo para este escenario
        int conteoAntes = t.stackingItems().length;
        t.pushLid(6, "evil"); // tapa cup6 -> ambos desaparecen
        int conteoDespues = t.stackingItems().length;
        assertTrue("La EvilLid debe ser exitosa", t.ok());
        assertEquals(
                "EvilLid y su taza deben eliminarse (un elemento menos)",
                conteoAntes - 1, conteoDespues);

        int resp = JOptionPane.showConfirmDialog(null,
                "Has visto en el canvas:\n\n"
                        + "  A. FearfulLid rechazo entrar sin su taza (error mostrado)\n"
                        + "  B. FearfulLid entro correctamente con su taza presente\n"
                        + "  C. OpenerCup(4) destruyo las tapas con numero <= 4\n"
                        + "  D. EvilLid elimino a su taza y a ella misma al taparla\n\n"
                        + "¿Aceptas esta prueba?",
                "Confirmacion - Prueba 1", JOptionPane.YES_NO_OPTION);

        t.makeInvisible();
        assertTrue("El usuario rechazo la prueba del Simulador Polimorfico",
                resp == JOptionPane.YES_OPTION);
    }

    /**
     * Prueba 2: Algoritmo de Maraton (TowerContest).
     * Verifica que el solucionador encuentra el orden correcto para n=5, h=17
     * y lo anima en el canvas. Tambien verifica casos imposibles.
     */
    @Test
    public void acceptAlgoritmoMaraton() {
        TowerContest contest = new TowerContest();

        // Verificaciones logicas (no visuales)
        String solucion = contest.solve(5, 17);
        assertFalse("n=5, h=17 debe tener solucion", solucion.equals("impossible"));
        assertEquals("n=5, h=2  debe ser imposible", "impossible", contest.solve(5, 2));
        assertEquals("n=5, h=26 debe ser imposible", "impossible", contest.solve(5, 26));

        JOptionPane.showMessageDialog(null,
                "PRUEBA 2: Algoritmo de Maraton (TowerContest)\n\n"
                        + "El algoritmo encontro la solucion para n=5, h=17:\n"
                        + "  " + solucion + "\n\n"
                        + "Tambien verifico correctamente:\n"
                        + "  n=5, h=2  -> impossible\n"
                        + "  n=5, h=26 -> impossible\n\n"
                        + "Al cerrar este dialogo se iniciara la animacion en el canvas.\n"
                        + "Observa como se construye la torre taza por taza.",
                "Aceptacion - Prueba 2", JOptionPane.INFORMATION_MESSAGE);

        // Lanzar la animacion (corre en su propio hilo, muestra su propio dialogo al
        // terminar)
        contest.simulate(5, 17);

        int resp = JOptionPane.showConfirmDialog(null,
                "El algoritmo encontro:\n"
                        + "  Solucion: " + solucion + "\n\n"
                        + "La animacion esta corriendo (o termino) en el canvas.\n\n"
                        + "¿Aceptas esta prueba?",
                "Confirmacion - Prueba 2", JOptionPane.YES_NO_OPTION);

        assertTrue("El usuario rechazo la prueba del Algoritmo de Maraton",
                resp == JOptionPane.YES_OPTION);

        // Limpiar el canvas antes de que corra la siguiente prueba
        contest.cleanup();
    }
}
