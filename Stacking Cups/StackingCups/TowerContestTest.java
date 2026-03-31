package StackingCups;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la logica de concurso en TowerContest.
 *
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */
public class TowerContestTest {

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
