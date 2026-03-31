package StackingCups;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas base para la clase Tower.
 *
 * Se ejecutan en modo invisible para evitar dependencias visuales en QA.
 *
 * @author Carlos Felipe Jimenez Sposito
 * @version 2.0
 */
public class TestTower {
    private Tower tower;

    /**
     * Configura una torre de pruebas antes de cada caso.
     */
    @BeforeEach
    public void setUp(){
        tower = new Tower(21, 20);
    }

    /**
     * Limpia la referencia de la torre al terminar cada prueba.
     */
    @AfterEach
    public void tearDown(){
        tower = null;
    }

    /**
     * Verifica que la torre inicie vacia y con estado no exitoso.
     */
    @Test
    public void shouldStartEmpty(){
        assertEquals(0, tower.height());
        assertFalse(tower.ok());
        assertEquals(0, tower.stackingItems().length);
    }

    /**
     * Verifica insercion valida de una taza.
     */
    @Test
    public void shouldPushCupWhenValid(){
        tower.pushCup(5);

        assertTrue(tower.ok());
        assertEquals(1, tower.height());
        assertEquals("cup", tower.getElements().get(0).getType());
        assertEquals(5, tower.getElements().get(0).getId());
    }

    /**
     * Verifica rechazo de taza que excede el ancho de la torre.
     */
    @Test
    public void shouldRejectCupWhenTooWide(){
        tower.pushCup(12);

        assertFalse(tower.ok());
        assertEquals(0, tower.height());
    }

    /**
     * Verifica que se permita mismo id entre tipos distintos.
     */
    @Test
    public void shouldAllowSameIdAcrossDifferentTypes(){
        tower.pushCup(4);
        tower.pushLid(4);

        assertTrue(tower.ok());
        assertEquals(2, tower.height());
    }

    /**
     * Verifica que se rechace id duplicado dentro del mismo tipo.
     */
    @Test
    public void shouldRejectDuplicateIdForSameType(){
        tower.pushCup(4);
        tower.pushCup(4);

        assertFalse(tower.ok());
        assertEquals(1, tower.height());
    }

    /**
     * Verifica insercion valida de tapa.
     */
    @Test
    public void shouldPushLidWhenValid(){
        tower.pushLid(3);

        assertTrue(tower.ok());
        assertEquals(1, tower.height());
        assertEquals("lid", tower.getElements().get(0).getType());
        assertEquals(3, tower.getElements().get(0).getId());
    }

    /**
     * Verifica eliminacion de la ultima taza insertada.
     */
    @Test
    public void shouldPopLastCup(){
        tower.pushCup(5);
        tower.pushCup(4);
        tower.popCup();

        assertTrue(tower.ok());
        assertEquals(1, tower.height());
        assertEquals(5, tower.getElements().get(0).getId());
    }

    /**
     * Verifica eliminacion por tipo e id.
     */
    @Test
    public void shouldRemoveSpecificLidById(){
        tower.pushLid(2);
        tower.pushLid(3);
        tower.removeLid(2);

        assertTrue(tower.ok());
        assertEquals(1, tower.height());
        assertEquals(3, tower.getElements().get(0).getId());
    }

    /**
     * Verifica ordenamiento de mayor base a menor base.
     */
    @Test
    public void shouldOrderTowerByBaseDescending(){
        tower.pushCup(2);
        tower.pushCup(4);
        tower.pushCup(3);

        tower.orderTower();

        String[][] items = tower.stackingItems();
        assertTrue(tower.ok());
        assertEquals("4", items[0][1]);
        assertEquals("3", items[1][1]);
        assertEquals("2", items[2][1]);
    }

    /**
     * Verifica la operacion cover y consulta lidedCups.
     */
    @Test
    public void shouldCoverAndReturnLidedCups(){
        tower.pushCup(1);
        tower.pushCup(2);
        tower.pushLid(2);
        tower.pushLid(1);

        tower.cover();
        int[] lided = tower.lidedCups();

        assertTrue(tower.ok());
        assertEquals(2, lided.length);
        assertEquals(1, lided[0]);
        assertEquals(2, lided[1]);
    }

    /**
     * Verifica que el constructor por cups cree el simulador vacio.
     */
    @Test
    public void shouldBuildTowerByCupsConstructor(){
        Tower autoTower = new Tower(4);
        assertEquals(0, autoTower.height());
    }
}
