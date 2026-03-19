package StackingCups;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The test class TestTower.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class TestTower
{
    private Tower tower;

    /**
     * Default constructor for test class TestTower
     */
    public TestTower()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
        tower = new Tower(21, 20);
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
        tower = null;
    }

    // ============================================================
    // PRUEBAS DE ESTADO INICIAL
    // ============================================================

    @Test
    public void shouldStartEmpty(){
        assertEquals(0, tower.height());
        assertFalse(tower.ok());
        assertEquals(0, tower.stackingItems().length);
    }

    // ============================================================
    // PRUEBAS DE INSERCION DE COPAS
    // ============================================================

    @Test
    public void shouldPushCupWhenValid(){
        tower.pushCup(5);

        assertTrue(tower.ok());
        assertEquals(1, tower.height());
        assertEquals("cup", tower.getElements().get(0).getType());
        assertEquals(5, tower.getElements().get(0).getId());
    }

    @Test
    public void shouldRejectCupWhenTooWide(){
        tower.pushCup(12); // 2*12-1=23 > ancho 21

        assertFalse(tower.ok());
        assertEquals(0, tower.height());
    }

    @Test
    public void shouldRejectDuplicateIdAcrossElements(){
        tower.pushCup(4);
        tower.pushCup(4);

        assertFalse(tower.ok());
        assertEquals(1, tower.height());
    }

    // ============================================================
    // PRUEBAS DE INSERCION DE TAPAS
    // ============================================================

    @Test
    public void shouldPushLidWhenValid(){
        tower.pushLid(3);

        assertTrue(tower.ok());
        assertEquals(1, tower.height());
        assertEquals("lid", tower.getElements().get(0).getType());
        assertEquals(3, tower.getElements().get(0).getId());
    }

    @Test
    public void shouldRejectLidWhenTooWide(){
        tower.pushLid(11); // 2*11-1=21 cabe
        assertTrue(tower.ok());

        tower.pushLid(12); // no cabe
        assertFalse(tower.ok());
        assertEquals(1, tower.height());
    }

    // ============================================================
    // PRUEBAS DE ELIMINACION
    // ============================================================

    @Test
    public void shouldPopLastCup(){
        tower.pushCup(5);
        tower.pushCup(4);

        tower.popCup();

        assertTrue(tower.ok());
        assertEquals(1, tower.height());
        assertEquals(5, tower.getElements().get(0).getId());
    }

    @Test
    public void shouldPopLastLid(){
        tower.pushLid(4);
        tower.pushLid(3);

        tower.popLid();

        assertTrue(tower.ok());
        assertEquals(1, tower.height());
        assertEquals(4, tower.getElements().get(0).getId());
    }

    @Test
    public void shouldRemoveSpecificCupById(){
        tower.pushCup(5);
        tower.pushCup(4);
        tower.pushCup(3);

        tower.removeCup(4);

        assertTrue(tower.ok());
        assertEquals(2, tower.height());
        assertEquals(5, tower.getElements().get(0).getId());
        assertEquals(3, tower.getElements().get(1).getId());
    }

    @Test
    public void shouldFailRemovingMissingLid(){
        tower.pushCup(5);
        tower.removeLid(2);

        assertFalse(tower.ok());
        assertEquals(1, tower.height());
    }

    // ============================================================
    // PRUEBAS DE REORGANIZACION
    // ============================================================

    @Test
    public void shouldOrderTowerByWidthAndHeight(){
        tower.pushCup(5);
        tower.pushLid(4);
        tower.pushCup(3);

        tower.orderTower();

        assertTrue(tower.ok());
        assertEquals(3, tower.height());
        assertEquals(3, tower.getElements().get(0).getId());
        assertEquals(4, tower.getElements().get(1).getId());
        assertEquals(5, tower.getElements().get(2).getId());
    }

    @Test
    public void shouldReverseTowerOrder(){
        tower.pushCup(3);
        tower.pushLid(4);
        tower.pushCup(5);

        tower.reverseTower();

        assertTrue(tower.ok());
        assertEquals(5, tower.getElements().get(0).getId());
        assertEquals(4, tower.getElements().get(1).getId());
        assertEquals(3, tower.getElements().get(2).getId());
    }

    // ============================================================
    // PRUEBAS DE CONSULTA
    // ============================================================

    @Test
    public void shouldReturnStackingItemsInCurrentOrder(){
        tower.pushCup(5);
        tower.pushLid(4);

        String[][] items = tower.stackingItems();

        assertEquals(2, items.length);
        assertEquals("cup", items[0][0]);
        assertEquals("5", items[0][1]);
        assertEquals("lid", items[1][0]);
        assertEquals("4", items[1][1]);
    }
}
