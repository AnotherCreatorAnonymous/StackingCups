package StackingCups;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unificadas del proyecto StackingCups.
 *
 * Contiene los escenarios de Tower y TowerContest en una sola clase.
 *
 * @author Carlos Felipe Jimenez Sposito
 * @version 3.0
 */
public class StackingCupsUnifiedTest {
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

    /**
     * Verifica que swap intercambie dos elementos existentes.
     */
    @Test
    public void shouldSwapTwoExistingElements(){
        tower.pushCup(2);
        tower.pushLid(2);

        tower.swap(new String[]{"cup", "2"}, new String[]{"lid", "2"});

        assertTrue(tower.ok());
        assertEquals(2, tower.height());
    }

    /**
     * Verifica fallo de swap cuando un elemento no existe.
     */
    @Test
    public void shouldFailSwapWhenElementDoesNotExist(){
        tower.pushCup(2);

        tower.swap(new String[]{"cup", "2"}, new String[]{"lid", "9"});

        assertFalse(tower.ok());
        assertEquals(1, tower.height());
    }

    /**
     * Verifica que swapToReduce retorne estructura valida de intercambio.
     */
    @Test
    public void shouldSuggestSwapToReduce(){
        tower.pushCup(1);
        tower.pushCup(3);
        tower.pushLid(2);

        String[][] move = tower.swapToReduce();

        assertEquals(2, move.length);
        assertEquals(2, move[0].length);
        assertEquals(2, move[1].length);
    }

    /**
     * Verifica que cover falle cuando no existen parejas cup-lid.
     */
    @Test
    public void shouldFailCoverWithoutPairs(){
        tower.pushCup(4);
        tower.cover();

        assertFalse(tower.ok());
    }

    /**
     * Verifica caso positivo de solucion para parametros validos.
     */
    @Test
    public void shouldSolvePossibleCase(){
        TowerContest contest = new TowerContest();
        assertEquals("possible", contest.solve(4, 6));
    }

    /**
     * Verifica caso negativo de solucion por altura insuficiente.
     */
    @Test
    public void shouldSolveImpossibleCase(){
        TowerContest contest = new TowerContest();
        assertEquals("impossible", contest.solve(5, 3));
    }

    /**
     * Verifica caso negativo con entradas invalidas.
     */
    @Test
    public void shouldRejectInvalidInput(){
        TowerContest contest = new TowerContest();
        assertEquals("impossible", contest.solve(0, 0));
    }

    /**
     * Verifica que simulate no falle en caso sin solucion.
     */
    @Test
    public void shouldSimulateGracefullyWhenImpossible(){
        TowerContest contest = new TowerContest();
        assertDoesNotThrow(() -> contest.simulate(5, 2));
    }
}