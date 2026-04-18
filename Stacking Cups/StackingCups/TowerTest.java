package StackingCups;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JOptionPane;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import shapes.Canvas;

/**
 * Pruebas unificadas del proyecto StackingCups.
 *
 * Incluye pruebas unitarias invisibles y pruebas de aceptacion manual
 * para validar el comportamiento visual del simulador.
 *
 * @author Carlos Felipe Jimenez Sposito
 * @version 4.0
 */
public class TowerTest {

    private Tower tower;

    // --- CICLO 1 ---
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

    // --- CICLO 2 ---
    @Test
    public void shouldStartEmpty(){
        assertEquals(0, tower.height());
        assertFalse(tower.ok());
        assertEquals(0, tower.stackingItems().length);
    }

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
        tower.pushCup(12);

        assertFalse(tower.ok());
        assertEquals(0, tower.height());
    }

    @Test
    public void shouldAllowSameIdAcrossDifferentTypes(){
        tower.pushCup(4);
        tower.pushLid(4);

        assertTrue(tower.ok());
        assertEquals(2, tower.height());
    }

    @Test
    public void shouldRejectDuplicateIdForSameType(){
        tower.pushCup(4);
        tower.pushCup(4);

        assertFalse(tower.ok());
        assertEquals(1, tower.height());
    }

    @Test
    public void shouldPushLidWhenValid(){
        tower.pushLid(3);

        assertTrue(tower.ok());
        assertEquals(1, tower.height());
        assertEquals("lid", tower.getElements().get(0).getType());
        assertEquals(3, tower.getElements().get(0).getId());
    }

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
    public void shouldRemoveSpecificLidById(){
        tower.pushLid(2);
        tower.pushLid(3);
        tower.removeLid(2);

        assertTrue(tower.ok());
        assertEquals(1, tower.height());
        assertEquals(3, tower.getElements().get(0).getId());
    }

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

    @Test
    public void shouldBuildTowerByCupsConstructor(){
        Tower autoTower = new Tower(4);
        assertEquals(0, autoTower.height());
    }

    @Test
    public void shouldSwapTwoExistingElements(){
        tower.pushCup(2);
        tower.pushLid(2);

        tower.swap(new String[]{"cup", "2"}, new String[]{"lid", "2"});

        assertTrue(tower.ok());
        assertEquals(2, tower.height());
    }

    @Test
    public void shouldFailSwapWhenElementDoesNotExist(){
        tower.pushCup(2);

        tower.swap(new String[]{"cup", "2"}, new String[]{"lid", "9"});

        assertFalse(tower.ok());
        assertEquals(1, tower.height());
    }

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

    @Test
    public void shouldFailCoverWithoutPairs(){
        tower.pushCup(4);
        tower.cover();

        assertFalse(tower.ok());
    }

    // --- CICLO 3 ---
    @Test
    public void shouldSolvePossibleCase(){
        TowerContest contest = new TowerContest();
        assertEquals("possible", contest.solve(4, 6));
    }

    @Test
    public void shouldSolveImpossibleCase(){
        TowerContest contest = new TowerContest();
        assertEquals("impossible", contest.solve(5, 3));
    }

    @Test
    public void shouldRejectInvalidInput(){
        TowerContest contest = new TowerContest();
        assertEquals("impossible", contest.solve(0, 0));
    }

    @Test
    public void shouldSimulateGracefullyWhenImpossible(){
        TowerContest contest = new TowerContest();
        assertDoesNotThrow(() -> contest.simulate(5, 2));
    }

    // --- CICLO 4 ---
    @Test
    public void shouldShiftSmallerElementsWithHierarchical(){
        tower.pushCup(5);
        tower.pushCup(1);
        int beforeSmallCupLevel = tower.logicalBaseLevel("cup", 1);

        tower.pushHierarchical(3);
        int afterSmallCupLevel = tower.logicalBaseLevel("cup", 1);

        String[][] items = tower.stackingItems();
        int idxHierarchical = indexOf(items, "hierarchical", "3");
        int idxSmallCup = indexOf(items, "cup", "1");

        assertTrue(tower.ok());
        assertTrue(beforeSmallCupLevel >= 0);
        assertTrue(afterSmallCupLevel >= 0);
        assertTrue(afterSmallCupLevel < beforeSmallCupLevel);
        assertTrue(idxHierarchical >= 0);
        assertTrue(idxSmallCup >= 0);
        assertTrue(idxHierarchical < idxSmallCup);
    }

    @Test
    public void shouldAccumulateLiftAcrossMultipleHierarchicalInsertions(){
        tower.pushCup(5);
        tower.pushCup(1);

        int initialLevel = tower.logicalBaseLevel("cup", 1);

        tower.pushHierarchical(4);
        int afterFirstHierarchical = tower.logicalBaseLevel("cup", 1);

        tower.pushHierarchical(3);
        int afterSecondHierarchical = tower.logicalBaseLevel("cup", 1);

        assertTrue(tower.ok());
        assertTrue(initialLevel >= 0);
        assertTrue(afterFirstHierarchical >= 0);
        assertTrue(afterSecondHierarchical >= 0);
        assertTrue(afterFirstHierarchical < initialLevel);
        assertTrue(afterSecondHierarchical < afterFirstHierarchical);
    }

    @Test
    public void shouldRollbackWhenHierarchicalLiftCannotFitHeight(){
        Tower limitedTower = new Tower(11, 2);

        limitedTower.pushCup(5);
        limitedTower.pushCup(1);
        limitedTower.pushHierarchical(4);

        int levelBeforeFailure = limitedTower.logicalBaseLevel("cup", 1);
        int heightBeforeFailure = limitedTower.height();

        limitedTower.pushHierarchical(3);

        assertFalse(limitedTower.ok());
        assertEquals(heightBeforeFailure, limitedTower.height());
        assertEquals(levelBeforeFailure, limitedTower.logicalBaseLevel("cup", 1));
        assertEquals(-1, limitedTower.logicalBaseLevel("hierarchical", 3));
    }

    @Test
    public void shouldPlaceCrazyAsBaseOfCompanionCup(){
        tower.pushCup(3);
        tower.pushCrazy(3);

        String[][] items = tower.stackingItems();

        assertTrue(tower.ok());
        assertEquals("crazy", items[0][0]);
        assertEquals("3", items[0][1]);
        assertEquals("cup", items[1][0]);
        assertEquals("3", items[1][1]);
    }

    @Test
    public void shouldRequireCompanionCupForFearful(){
        tower.pushFearful(7);

        assertFalse(tower.ok());
        assertEquals(0, tower.height());
    }

    @Test
    public void shouldAcceptFearfulWhenCompanionExists(){
        tower.pushCup(7);
        tower.pushFearful(7);

        assertTrue(tower.ok());
        assertEquals(2, tower.height());
    }

    @Test
    public void shouldRemoveOnlyBlockingLidsWhenOpenerEnters(){
        tower.pushCup(5);
        tower.pushLid(1);
        tower.pushLid(5);

        tower.pushOpener(3);

        long lids = tower.getElements().stream()
            .filter(e -> e.getKind() == StackableElement.ElementKind.LID)
            .count();
        boolean hasLid5 = tower.getElements().stream()
            .anyMatch(e -> e.getType().equals("lid") && e.getId() == 5);
        boolean hasLid1 = tower.getElements().stream()
            .anyMatch(e -> e.getType().equals("lid") && e.getId() == 1);

        assertTrue(tower.ok());
        assertEquals(1, lids);
        assertTrue(hasLid5);
        assertFalse(hasLid1);
    }

    // --- ACEPTACION ---
    /**
     * Prueba manual visual del comportamiento hierarchical y crazy.
     */
    @Disabled("Prueba manual de aceptacion visual")
    @Test
    public void acceptanceShouldDisplayHierarchicalAndCrazy(){
        Tower visual = new Tower(21, 20);
        visual.pushCup(5);
        visual.pushCup(2);
        visual.pushHierarchical(3);
        visual.pushCrazy(5);
        visual.makeVisible();

        Canvas.getCanvas().wait(800);
        JOptionPane.showMessageDialog(null,
            "Verifica que la crazy(5) quede como base de la cup(5) y que hierarchical(3) eleve menores.");

        assertTrue(visual.ok());
        visual.makeInvisible();
    }

    /**
     * Prueba manual visual del comportamiento opener.
     */
    @Disabled("Prueba manual de aceptacion visual")
    @Test
    public void acceptanceShouldDisplayOpenerCleaningLids(){
        Tower visual = new Tower(21, 20);
        visual.pushCup(5);
        visual.pushLid(1);
        visual.pushLid(5);
        visual.makeVisible();

        Canvas.getCanvas().wait(500);
        visual.pushOpener(3);
        Canvas.getCanvas().wait(500);

        JOptionPane.showMessageDialog(null,
            "Verifica que opener(3) quite solo tapas que bloquean su paso y conserve tapas no bloqueantes.");

        assertTrue(visual.ok());
        visual.makeInvisible();
    }

    /**
     * Busca la posicion de un par tipo-id en la matriz de items.
     */
    private int indexOf(String[][] items, String type, String id){
        for(int i = 0; i < items.length; i++){
            if(type.equals(items[i][0]) && id.equals(items[i][1])){
                return i;
            }
        }
        return -1;
    }
}
