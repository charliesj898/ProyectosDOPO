package tower;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad para el Ciclo 4 de Tower.
 * Cubre el comportamiento de los nuevos tipos polimorficos:
 * OpenerCup, HierarchicalCup, FearfulLid, CrazyLid y EvilLid.
 *
 * @author Carlos Sanchez, Samuel Argalle
 * @version 4.0
 */
public class TowerC4Test {

    private Tower tower;

    @Before
    public void setUp() {
        // Torre con espacio amplio para todas las pruebas
        tower = new Tower(30, 60);
    }

    // ---- OpenerCup ----

    @Test
    public void openerCupShouldDestroyBlockingLid() {
        // Que deberia hacer: destruir la tapa con la que colisiona fisicamente al caer
        tower.pushCup(3);
        tower.pushLid(2); // tapa 2 queda dentro de cup 3 (mas baja)
        tower.pushLid(3); // tapa 3 queda encima de tapa 2 (la OpenerCup chocara con ella)
        tower.pushCup(4, "opener"); // choca con lid 3, la destruye; lid 2 queda intacta

        boolean lid3Queda = false;
        for (String[] item : tower.stackingItems()) {
            if (item[0].equals("lid") && item[1].equals("3")) lid3Queda = true;
        }
        assertFalse("Lid 3 debio destruirse por colision con OpenerCup 4", lid3Queda);

        // Lid 2 queda porque no estaba en el camino de colision directo
        boolean lid2Queda = false;
        for (String[] item : tower.stackingItems()) {
            if (item[0].equals("lid") && item[1].equals("2")) lid2Queda = true;
        }
        assertTrue("Lid 2 debe permanecer (no estaba bloqueando el paso)", lid2Queda);
        assertTrue(tower.ok());
    }


    // ---- HierarchicalCup ----

    @Test
    public void hierarchicalCupShouldBehaveNormallyWhenNotOnFloor() {
        // Que deberia hacer: comportarse como una taza normal si no toca el suelo
        tower.pushCup(3); // taza normal en el suelo
        tower.pushCup(2, "hierarchical"); // HierarchicalCup encima, no toca suelo

        tower.popCup(); // deberia poder eliminarse (no esta en Y=0)
        assertTrue(tower.ok());
    }

    @Test
    public void hierarchicalCupShouldRefusePopWhenOnFloor() {
        // Que no deberia hacer: permitir ser eliminada si toca el suelo (Y=0)
        // Tower con ancho justo para que la HierarchicalCup sea la mas grande y toque
        // suelo
        Tower strictTower = new Tower(10, 30);
        strictTower.pushCup(4, "hierarchical"); // Es la mayor, descansa en Y=0

        strictTower.popCup(); // debe fallar
        assertFalse(strictTower.ok());

        // Debe seguir en la torre
        assertEquals(1, strictTower.stackingItems().length);
    }

    @Test
    public void hierarchicalCupShouldRefuseRemoveWhenOnFloor() {
        // Que no deberia hacer: permitir ser removida por numero si toca el suelo
        Tower strictTower = new Tower(10, 30);
        strictTower.pushCup(4, "hierarchical");

        strictTower.removeCup(4); // debe fallar
        assertFalse(strictTower.ok());
        assertEquals(1, strictTower.stackingItems().length);
    }

    // ---- FearfulLid ----

    @Test
    public void fearfulLidShouldEnterIfMasterCupExists() {
        // Que deberia hacer: entrar si su taza ya esta en la torre
        tower.pushCup(3);
        tower.pushLid(3, "fearful");

        assertTrue(tower.ok());
        int[] lided = tower.lidedCups();
        assertEquals(1, lided.length);
        assertEquals(3, lided[0]);
    }

    @Test
    public void fearfulLidShouldRefuseEntryIfMasterCupMissing() {
        // Que no deberia hacer: entrar si su taza no esta en la torre
        tower.pushLid(3, "fearful"); // la taza 3 no existe aun

        assertFalse(tower.ok());
        assertEquals(0, tower.stackingItems().length);
    }

    @Test
    public void fearfulLidShouldRefusePopWhenCoveringMaster() {
        // Que no deberia hacer: dejarse quitar si esta tapando a su taza
        tower.pushCup(3);
        tower.pushLid(3, "fearful");
        tower.cover(); // asegurar que cubre a la taza 3

        tower.popLid(); // debe fallar
        assertFalse(tower.ok());
    }

    @Test
    public void fearfulLidShouldAllowPopWhenNotCovering() {
        // Que deberia hacer: dejarse quitar si no esta cubriendo a su taza
        tower.pushCup(3);
        tower.pushLid(3, "fearful"); // entra porque su taza esta

        // Quitamos la taza primero (para que la FearfulLid no este cubriendo nada)
        // No podemos, porque la lid ya la tapa. Usamos removeLid indirectamente:
        // Insertemos una FearfulLid en estado NO cubriendo (si esta encima de otra
        // tapa)
        tower.pushCup(5);
        tower.pushLid(5); // tapa normal encima
        tower.pushLid(3, "fearful"); // tapa 3 queda encima de tapa 5, no cubre taza 3

        tower.popLid(); // quita la FearfulLid que no esta cubriendo -> debe funcionar
        assertTrue(tower.ok());
    }

    // ---- CrazyLid ----

    @Test
    public void crazyLidShouldLiftMasterCupWhenCovering() {
        // Que deberia hacer: introducirse bajo su taza y elevarla 1 cm
        Tower t = new Tower(20, 50);
        t.pushCup(3); // taza 3 en Y=0, altura total = 5 cm
        int heightBefore = t.height();

        t.pushLid(3, "crazy"); // CrazyLid 3: se mete bajo la taza 3, la empuja +1
        int heightAfter = t.height();

        assertTrue("La CrazyLid debe elevar la torre", heightAfter > heightBefore);
        assertTrue(t.ok());
    }

    @Test
    public void crazyLidShouldActAsNormalLidWithoutMasterCup() {
        // Que deberia hacer: comportarse como tapa normal si su taza no esta
        tower.pushCup(5); // solo hay taza 5
        tower.pushLid(3, "crazy"); // CrazyLid 3: no encuentra su taza, cae al suelo

        assertTrue(tower.ok()); // debe insertarse sin error
    }

    // ---- EvilLid ----

    @Test
    public void evilLidShouldDestroyCupAndItselfWhenCoveringMaster() {
        // Que deberia hacer: al tapar a su taza, ambos desaparecen
        tower.pushCup(3);
        int itemsBefore = tower.stackingItems().length; // 1 item

        tower.pushLid(3, "evil"); // evil lid tapa a cup 3 -> ambos se eliminan

        int itemsAfter = tower.stackingItems().length;
        assertEquals("La EvilLid y su taza deben eliminarse", itemsBefore - 1, itemsAfter);
        assertTrue(tower.ok());
    }

    @Test
    public void evilLidShouldNotDestroyOtherCups() {
        // Que no deberia hacer: eliminar tazas que no son la suya
        tower.pushCup(5);
        tower.pushCup(3);
        tower.pushLid(3, "evil"); // solo elimina taza 3 y a si misma, no la 5

        String[][] items = tower.stackingItems();
        boolean cup5Exists = false;
        for (String[] item : items) {
            if (item[0].equals("cup") && item[1].equals("5")) {
                cup5Exists = true;
            }
        }
        assertTrue("La taza 5 no debe ser afectada por la EvilLid 3", cup5Exists);
    }

    @Test
    public void evilLidShouldStayIfMasterCupMissing() {
        // Que deberia hacer: quedarse en la torre si su taza no esta presente
        tower.pushCup(5); // solo hay taza 5
        tower.pushLid(3, "evil"); // EvilLid 3: su taza no existe, no puede taparla

        // Como no tapo a nadie, debe quedarse en la torre
        String[][] items = tower.stackingItems();
        boolean evilExists = false;
        for (String[] item : items) {
            if (item[0].equals("lid") && item[1].equals("3")) {
                evilExists = true;
            }
        }
        assertTrue("La EvilLid debe permanecer si no pudo tapar a su taza", evilExists);
        assertTrue(tower.ok());
    }

    // ---- createCopy (polimorfismo) ----

    @Test
    public void createCopyShouldPreserveSubclassType() {
        // Que deberia hacer: createCopy() retorna la misma subclase
        OpenerCup opener = new OpenerCup(3, 10);
        StackableItem copy = opener.createCopy();
        assertTrue("createCopy de OpenerCup debe ser OpenerCup", copy instanceof OpenerCup);

        HierarchicalCup hier = new HierarchicalCup(2, 10);
        StackableItem copyH = hier.createCopy();
        assertTrue("createCopy de HierarchicalCup debe ser HierarchicalCup", copyH instanceof HierarchicalCup);

        EvilLid evil = new EvilLid(3, 10);
        StackableItem copyE = evil.createCopy();
        assertTrue("createCopy de EvilLid debe ser EvilLid", copyE instanceof EvilLid);
    }
}
