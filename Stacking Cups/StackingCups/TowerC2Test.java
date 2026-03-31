package StackingCups;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de ciclo 2 para funcionalidades avanzadas de Tower.
 *
 * Se validan escenarios positivos y negativos en modo invisible.
 *
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */
public class TowerC2Test {
    private Tower tower;

    /**
     * Inicializa la torre de pruebas antes de cada caso.
     */
    @BeforeEach
    public void setUp(){
        tower = new Tower(21, 20);
    }

    /**
     * Libera la referencia de la torre al finalizar cada caso.
     */
    @AfterEach
    public void tearDown(){
        tower = null;
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
}
