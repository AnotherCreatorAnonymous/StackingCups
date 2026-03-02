package StackingCups;

import shapes.*;
import java.util.*;

/**
 * Simulador StackingCups
 * Implementa representación matricial en bloques.
 * Separación entre modelo lógico (Tower) y visualización.
 * 
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */


public class StackingCups {

    private Tower tower;

    // Visual
    private List<Rectangle> drawnBlocks;
    private boolean visible;

    private static final int CELL = 30;
    private static final int ROWS = 40;
    private static final int COLS = 40;
    private static final int BASE_X = 50;
    private static final int BASE_Y = 600;

    private int[][] grid;

    // ================= CONSTRUCTOR =================

    public StackingCups() {
        tower = new Tower(20, 20);
        drawnBlocks = new ArrayList<>();
        grid = new int[ROWS][COLS];
        visible = false;
    }

    // =================================================
    // ================= OPERACIONES ===================
    // =================================================

    public void pushCup(int n) {
        Cup cup = new Cup(n);
        tower.push(cup);
        redrawIfVisible();
    }

    public void pushLid(int n) {
        Lid lid = new Lid(n);
        tower.push(lid);
        redrawIfVisible();
    }

    public void popCup() {
        tower.popLastOfType("cup");
        redrawIfVisible();
    }

    public void popLid() {
        tower.popLastOfType("lid");
        redrawIfVisible();
    }

    public void removeCup(int n) {
        tower.removeByNumber("cup", n);
        redrawIfVisible();
    }

    public void removeLid(int n) {
        tower.removeByNumber("lid", n);
        redrawIfVisible();
    }

    public void orderTower() {
        tower.orderDescending();
        redrawIfVisible();
    }

    public void reverseTower() {
        tower.reverse();
        redrawIfVisible();
    }

    public int height() {
        return tower.height();
    }

    public String[][] stackingItems() {
        return tower.stackingItems();
    }

    public boolean ok() {
        return tower.ok();
    }

    // =================================================
    // ================= VISUAL ========================
    // =================================================

    public void makeVisible() {
        visible = true;
        draw();
    }

    public void makeInvisible() {
        visible = false;
        clearDrawing();
    }

    public void exit() {
        makeInvisible();
    }

    private void redrawIfVisible() {
        if (visible) {
            draw();
        }
    }

    // =================================================
    // ================= GRID LOGICO ===================
    // =================================================

    private void clearGrid() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                grid[r][c] = 0;
    }

    private void buildGrid() {

        clearGrid();

        int currentRow = 0;
        int center = COLS / 2;

        for (StackableElement element : tower.getElements()) {

            int number = element.getNumber();
            int widthUnits = number * 2; // ancho proporcional al número

            int left = center - widthUnits / 2;
            int right = left + widthUnits - 1;

            // paredes
            for (int h = 0; h < number; h++) {

                int row = currentRow + h;

                if (row >= ROWS) break;

                grid[row][left] = 1;
                grid[row][right] = 1;
            }

            // base
            if (currentRow < ROWS) {
                for (int c = left; c <= right; c++) {
                    if (c >= 0 && c < COLS)
                        grid[currentRow][c] = 1;
                }
            }

            currentRow += number;
        }
    }

    private void drawGrid() {

        clearDrawing();

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {

                if (grid[r][c] == 1) {

                    Rectangle block = new Rectangle();
                    block.changeSize(CELL, CELL);

                    int x = BASE_X + c * CELL;
                    int y = BASE_Y - r * CELL;

                    block.changePosition(x, y);
                    block.makeVisible();

                    drawnBlocks.add(block);
                }
            }
        }
    }

    public void draw() {
        buildGrid();
        drawGrid();
    }

    private void clearDrawing() {
        for (Rectangle r : drawnBlocks)
            r.makeInvisible();
        drawnBlocks.clear();
    }
}
