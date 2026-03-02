import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad requeridas para el Ciclo 2 de Tower.
 * Siguiendo la premisa de "No olvidar diseñar preguntas: q deberia y q no
 * deberia hacer"
 */
public class TowerC2Test {

    private Tower tower;

    @Before
    public void setUp() {
        // Inicializar sin makeVisible() para que corran completamente invsibles.
        tower = new Tower(4);
    }

    @Test
    public void accordingASShouldCreateTowerWithCups() {
        // Que deberia hacer: crear 4 tazas y apilarlas adecuadamente.
        assertEquals(16, tower.height()); // Altura sumando tazas 1, 2, 3, 4 = 1+3+5+7 = 16
        String[][] items = tower.stackingItems();
        assertEquals(4, items.length);
        assertEquals("4", items[3][1]); // Ultima taza apilada en la cima es la n° 4
        assertTrue(tower.ok());
    }

    @Test
    public void accordingASShouldCoverLids() {
        // Que deberia hacer: Al introducir tapas en cualquier orden, cover() las sitúa
        // con precisión
        tower.pushLid(2);
        tower.pushLid(4);
        tower.cover(); // Asegura el pareo geometrico

        int[] lided = tower.lidedCups();
        assertEquals(2, lided.length);
        assertTrue(tower.ok());
    }

    @Test
    public void accordingASShouldSwapToReduceReturnsValid() {
        // Que deberia hacer: Retornar algun swap asumiendo q pusimos del menor a mayor
        // al principio,
        // (1, 2, 3, 4) lo que da la altura maxima de combinacion. Intercambiarlos
        // reduce.
        String[][] suggestedSwap = tower.swapToReduce();
        assertNotNull(suggestedSwap);
    }

    @Test
    public void accordingASShouldInvalidSwapRevertsGracefully() {
        // Que NO deberia hacer: Permitir un swap que físicamente rompa las
        // restricciones
        Tower strictTower = new Tower(20, 9); // Altura maxima estricta de 9
        strictTower.pushCup(4); // alt = 7 (y=0) -> altura total 7
        strictTower.pushCup(2); // alt = 3 (cae dentro de la taza 4, y=1) -> altura total max(7, 4) = 7

        // Comprobamos que hasta aqui todo bien
        assertTrue(strictTower.ok());

        // Si invertimos y forzamos el 4 a subir sobre el 2
        // El 4 (alt 7) tendria que apoyarse en los bordes del 2 (alt 3). Nueva altura =
        // 3 + 7 = 10.
        // 10 excede el limite de 9. El swap deberia fallar y revertirse.
        String[] o1 = { "cup", "4" };
        String[] o2 = { "cup", "2" };
        strictTower.swap(o1, o2);

        // Deberia haber retornado false al fallar
        assertFalse(strictTower.ok());

        // La altura deberia mantenerse intacta en 7
        assertEquals(7, strictTower.height());
    }
}
