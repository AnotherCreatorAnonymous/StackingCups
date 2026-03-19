package StackingCups;
import java.util.*;
import javax.swing.*;
import shapes.*;
    
/**
 * Write a description of class Tower here.
 * 
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */
    
    public class Tower {
    
    private final List<StackableElement> elements;
    private boolean lastOperationOk;
    private final int width;
    private boolean isVisible;
    
    private final StackableElement[][] grid;
    private final int rows;
    private final int cols;
    private static final int CELL_SIZE = 10;
    private static final int METER_BAR_WIDTH = 8;
    private static final int BASE_THICKNESS = 4;
    private static final int METER_GAP = 20;

    private final Rectangle[] heightMeter;
    private final Rectangle heightPointer;
    private final Rectangle baseLine;
    private final Rectangle baseMarker;
    private final int originX;
    private final int baseY;
    private final int meterX;
    
    
    /**
     * Constructor de la clase torre
     */
    public Tower(int width, int maxHeight) {
        elements = new ArrayList<>();
        this. width = width;
        
        this.cols = width;         // ancho lógico
        this.rows = maxHeight;     // altura máxima
        grid = new StackableElement[rows][cols];

        int towerPixelWidth = cols * CELL_SIZE;
        int meterPixelWidth = METER_BAR_WIDTH + 12;
        int sceneWidth = towerPixelWidth + METER_GAP + meterPixelWidth;
        int sceneHeight = rows * CELL_SIZE + BASE_THICKNESS;

        originX = Math.max(0, (Canvas.CANVAS_WIDTH - sceneWidth) / 2);
        int originY = Math.max(0, (Canvas.CANVAS_HEIGHT - sceneHeight) / 2);
        baseY = originY + rows * CELL_SIZE;
        meterX = originX + towerPixelWidth + METER_GAP;

        heightMeter = new Rectangle[rows];
        for(int level = 0; level < rows; level++){
            heightMeter[level] = new Rectangle();
            heightMeter[level].changeSize(CELL_SIZE - 1, METER_BAR_WIDTH);
            heightMeter[level].changeColor("lightGray");
        }

        heightPointer = new Rectangle();
        heightPointer.changeSize(2, METER_BAR_WIDTH + 4);
        heightPointer.changeColor("black");

        baseLine = new Rectangle();
        baseLine.changeSize(BASE_THICKNESS, (cols * CELL_SIZE) + METER_GAP + METER_BAR_WIDTH + 12);
        baseLine.changeColor("brown");

        baseMarker = new Rectangle();
        baseMarker.changeSize(CELL_SIZE, METER_BAR_WIDTH + 10);
        baseMarker.changeColor("maroon");
    }

    
    /**
     * ===================================================================
     * ManageCup
     * ===================================================================
     */
    /**
     * Agrega una copa a la torre si cumple validaciones de tamano, id y espacio.
     */
    public void pushCup(int n){
        int w = (2*n - 1);

        if(!validateElement(n)){
            return;
        }
    
        int level = findLevelForCup(w);
    
        if(level == -1){
            lastOperationOk = false;
            JOptionPane.showMessageDialog(null, "No hay espacio para la copa");
            return;
        }
    
        Cup cup = new Cup(n);
        elements.add(cup);
    
        placeCupInGrid(cup, level, w);
        repaint();
        lastOperationOk = true;
    }
    
    /**
     * Elimina la ultima copa agregada a la torre.
     */
    public void popCup(){
        validateList("cup");
    }
    
    /**
     * Elimina una copa especifica por id.
     */
    public void removeCup(int n){
        removeElement("cup", n);
    }
    
    /**
     * ===================================================================
     * ManageLid
     * ===================================================================
     */
    
    /**
     * Agrega una tapa a la torre si cumple validaciones de tamano, id y espacio.
     */
    public void pushLid(int n) {

        if (validateElement(n)){
            int w = (2*n - 1);
            int level = findLevelForCup(w);

            if(level == -1){
                lastOperationOk = false;
                JOptionPane.showMessageDialog(null, "No hay espacio para la tapa");
                return;
            }

            Lid lid= new Lid(n);
            elements.add(lid);
            placeCupInGrid(lid, level, w);
            repaint();
            lastOperationOk = true;
        }
    }
    
    /**
     * Elimina la ultima tapa agregada a la torre.
     */
    public void popLid(){
        validateList("lid");
    }
    
    /**
     * Elimina una tapa especifica por id.
     */
    public void removeLid(int n){
        removeElement("lid", n);
    }
    
    /**
     * ===================================================================
     * Manage Aditions
     * ===================================================================
     */
    /**
     * Valida que un elemento pueda entrar en la torre y que su id no exista.
     */
    private boolean validateElement(int n){
        
        if (2*n-1 > width){
            lastOperationOk = false;
            JOptionPane.showMessageDialog(null, "El elemento excede el ancho de la torre");
            return false;
        }
        for (StackableElement e: elements){
            if (e.getId() == n){
                lastOperationOk = false;
                JOptionPane.showMessageDialog(null, "Ya existe un elemento con ese id");
                return false;
            }
        }
        lastOperationOk = true;
        return true;
    
    }
    
    /**
     * Valida que exista al menos un elemento del tipo solicitado para eliminar.
     */
    private boolean validateList(String type){
        if (!elements.isEmpty()){
            return deleteLastElement(type);
        }
        else{
            lastOperationOk = false;
            JOptionPane.showMessageDialog(null, "La torre esta vacia");
            return false;
        }
    }
    
    /**
     * Elimina el ultimo elemento que coincida con el tipo solicitado.
     */
    private boolean deleteLastElement(String type){
        boolean found = false;
        for (int e = elements.size() - 1; e >= 0; e--){
            StackableElement actualElement = elements.get(e);
            if (actualElement.getType().equalsIgnoreCase(type)){
                actualElement.makeInvisible();
                elements.remove(e);
                removeFromGrid(actualElement);
                repaint();
                found = true;
                lastOperationOk = true;
                break;
            }
        }
        if(!found){
            lastOperationOk = false;
            JOptionPane.showMessageDialog(null, "No se encontró un elemento tipo: " + type);
        }
        return found;
    }
    
    /**
     * Elimina un elemento por tipo e id manteniendo consistencia en lista y grilla.
     */
    private void removeElement(String type, int n){
        for (int e = 0; e < elements.size(); e++){
            StackableElement actualElement = elements.get(e);
            if (actualElement.getType().equalsIgnoreCase(type) && actualElement.getId() == n){
                actualElement.makeInvisible();
                elements.remove(e);
                removeFromGrid(actualElement);
                repaint();
                lastOperationOk = true;
                return;
            }
        }
        lastOperationOk = false;
        JOptionPane.showMessageDialog(null, "No se encontró el elemento a eliminar");
    }

    /**
     * ===================================================================
     * Reorganize tower
     * ===================================================================
     */
    
    /**
     * Ordena los elementos por ancho y luego por altura.
     */
    public void orderTower(){
        for(int e = 0; e < elements.size() - 1; e++){
            for(int n = e + 1; n < elements.size(); n++){
                if ((elements.get(e).getWidth() > elements.get(n).getWidth()) || (elements.get(e).getWidth() == elements.get(n).getWidth() && elements.get(e).getHeight() > elements.get(n).getHeight())){
                    StackableElement temp = elements.get(n);
                    elements.set(n, elements.get(e));
                    elements.set(e, temp);
                }
            }
        }
        lastOperationOk = rebuildGridFromElements();
        repaint();
    }
    
    /**
     * Invierte el orden actual de los elementos.
     */
    public void reverseTower(){
        for(int e = 0; e < elements.size()/2; e++){
            StackableElement temp = elements.get((elements.size() - 1) - e);
            elements.set((elements.size() - 1) - e, elements.get(e));
            elements.set(e, temp);
        }
        lastOperationOk = rebuildGridFromElements();
        repaint();
    }
        
    /**
     * ===================================================================
     * Consult Information
     * ===================================================================
     */
    /**
     * Retorna la cantidad de elementos actualmente almacenados.
     */
    public int height() {
        return elements.size();
    }

    /**
     * Retorna el resultado de la ultima operacion realizada.
     */
    public boolean ok(){
        return lastOperationOk;
    }

    /**
     * Retorna un arreglo con tipo e id de cada elemento en orden actual.
     */
    public String[][] stackingItems() {

        String[][] result = new String[elements.size()][2];

        for (int i = 0; i < elements.size(); i++) {
            result[i][0] = elements.get(i).getType();
            result[i][1] = String.valueOf(elements.get(i).getId());
        }

        return result;
    }

    /**
     * Retorna la lista interna de elementos de la torre.
     */
    public List<StackableElement> getElements() {
        return elements;
    }
    
    /**
     * ===================================================================
     * Set Visibility
     * ===================================================================
     */
    /**
     * Hace visible la torre y repinta su estado actual.
     */
    public void makeVisible(){
        isVisible = true;
        repaint();
    }
    
    /**
     * Hace invisible la torre y borra sus elementos del canvas.
     */
    public void makeInvisible() {
        for (StackableElement e : elements) {
            e.erase(); 
        }

        for(int level = 0; level < rows; level++){
            heightMeter[level].makeInvisible();
        }
        heightPointer.makeInvisible();
        baseLine.makeInvisible();
        baseMarker.makeInvisible();

        this.isVisible = false;
    }
    
    /**
     * ===================================================================
     * Exit simulator
     * ===================================================================
     */
    /**
     * Cierra el simulador de forma controlada.
     */
    public void exit() {
        makeInvisible();

        int confirm = JOptionPane.showConfirmDialog(null, 
            "¿Deseas cerrar el simulador de Stacking Cups?", 
            "Cerrar Simulador", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0); 
        }
        
        lastOperationOk = true;
    }
    
    /**
     * ===================================================================
     * Painting
     * ===================================================================
     */
    
    /**
     * Redibuja todos los elementos visibles segun su posicion en la grilla.
     */
    public void repaint(){
        if(!isVisible) return;
    
        // limpiar
        for(StackableElement e : elements){
            e.erase();
        }
    
        Set<StackableElement> drawn = new HashSet<>();
    
        for(int y = rows - 1; y >= 0; y--){
            for(int x = 0; x < cols; x++){
    
                StackableElement e = grid[y][x];
    
                if(e != null && !drawn.contains(e)){
    
                    int pixelX = originX + (x * CELL_SIZE);
                    int pixelY = levelToPixelY(y, e.getHeight());
    
                    e.draw(pixelX, pixelY);
                    drawn.add(e);
                }
            }
        }

        drawBase();
        drawHeightMeter();
    }

    /**
     * Dibuja y mantiene visible la base fija de la torre.
     */
    private void drawBase(){
        baseLine.changePosition(originX, baseY);
        baseLine.makeVisible();

        baseMarker.changePosition(meterX - 6, baseY - (CELL_SIZE / 2));
        baseMarker.makeVisible();
    }

    /**
     * Dibuja un medidor lateral de altura con barras y un puntero del nivel actual.
     */
    private void drawHeightMeter(){
        int currentHeight = towerHeightLevels();
        int filledStartRow = rows - currentHeight;

        for(int level = 0; level < rows; level++){
            Rectangle bar = heightMeter[level];
            int y = baseY - ((rows - level) * CELL_SIZE);

            bar.changePosition(meterX, y);
            if(level >= filledStartRow){
                bar.changeColor("seagreen");
            }
            else{
                bar.changeColor("lightGray");
            }
            bar.makeVisible();
        }

        int pointerRow = (currentHeight == 0) ? rows - 1 : filledStartRow;
        int pointerY = baseY - ((rows - pointerRow) * CELL_SIZE);
        heightPointer.changePosition(meterX - 4, pointerY);
        heightPointer.makeVisible();
    }

    /**
     * Convierte un nivel logico de la grilla a coordenada Y en pixeles.
     * El fondo del elemento queda anclado al nivel para evitar dibujar bajo la base.
     */
    private int levelToPixelY(int level, int elementHeight){
        int levelsFromBase = (rows - 1) - level;
        return baseY - elementHeight - (levelsFromBase * CELL_SIZE);
    }

    /**
     * Retorna la altura fisica actual de la torre medida en niveles de grilla.
     */
    private int towerHeightLevels(){
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < cols; x++){
                if(grid[y][x] != null){
                    return rows - y;
                }
            }
        }
        return 0;
    }
    
    /**
     * Busca el nivel mas bajo posible para ubicar un elemento de ancho dado.
     */
    private int findLevelForCup(int cupWidth){
        int startX = (cols - cupWidth) / 2;

        if(startX < 0 || startX + cupWidth > cols){
            return -1;
        }
    
        for(int level = rows - 1; level >= 0; level--){
            if(canPlace(level, startX, cupWidth) && hasSupport(level, startX, cupWidth)){
                return level;
            }
        }
        return -1; // no cabe
    }
    
    /**
     * Verifica si un rango horizontal esta libre en un nivel de la grilla.
     */
    private boolean canPlace(int level, int startX, int width){
        for(int x = startX; x < startX + width; x++){
            if(grid[level][x] != null){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Verifica que el elemento tenga soporte completo debajo de su base.
     */
    private boolean hasSupport(int level, int startX, int width){
    
        // Si está en el piso, siempre válido
        if(level == rows - 1){
            return true;
        }
    
        // Verifica que haya algo debajo en TODA la base
        for(int x = startX; x < startX + width; x++){
            if(grid[level + 1][x] == null){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Marca en la grilla las celdas ocupadas por un elemento.
     */
    private void placeCupInGrid(StackableElement cup, int level, int width){
        int startX = (cols - width) / 2;
    
        for(int x = startX; x < startX + width; x++){
            grid[level][x] = cup;
        }
    }
    
    /**
     * Elimina de la grilla todas las celdas asociadas a un elemento.
     */
    private void removeFromGrid(StackableElement element){
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < cols; x++){
                if(grid[y][x] == element){
                    grid[y][x] = null;
                }
            }
        }
    }

    /**
     * Reconstruye la grilla completa a partir del orden actual de la lista.
     */
    private boolean rebuildGridFromElements(){
        clearGrid();

        for(StackableElement element : elements){
            int logicalWidth = element.getWidth() / CELL_SIZE;
            int level = findLevelForCup(logicalWidth);

            if(level == -1){
                JOptionPane.showMessageDialog(null, "No fue posible reorganizar toda la torre");
                return false;
            }

            placeCupInGrid(element, level, logicalWidth);
        }

        return true;
    }

    /**
     * Limpia por completo el estado ocupado de la grilla.
     */
    private void clearGrid(){
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < cols; x++){
                grid[y][x] = null;
            }
        }
    }
    
    /**
     * ===================================================================
     * CICLO DOS - Nuevas funcionalidades de la torre
     * ===================================================================
     */
    
    /**
     * CICLO DOS - Crea una torre inicial llenándola con el número de tazas deseadas.
     * Las tazas se crean en orden ascendente de tamaño.
     */
    public void tower(int cups){
        for(int i = 1; i <= cups; i++){
            pushCup(i);
        }
    }
    
    /**
     * CICLO DOS - Intercambia la posición de dos objetos en la torre.
     * Los parámetros contienen tipo e id de cada objeto a intercambiar.
     * 
     * @param o1 Array con [tipo, id] del primer objeto
     * @param o2 Array con [tipo, id] del segundo objeto
     */
    public void swap(String[] o1, String[] o2){
        StackableElement elem1 = null;
        StackableElement elem2 = null;
        
        for(StackableElement e : elements){
            if(e.getType().equalsIgnoreCase(o1[0]) && e.getId() == Integer.parseInt(o1[1])){
                elem1 = e;
            }
            if(e.getType().equalsIgnoreCase(o2[0]) && e.getId() == Integer.parseInt(o2[1])){
                elem2 = e;
            }
        }
        
        if(elem1 != null && elem2 != null){
            int idx1 = elements.indexOf(elem1);
            int idx2 = elements.indexOf(elem2);
            
            elements.set(idx1, elem2);
            elements.set(idx2, elem1);
            
            lastOperationOk = rebuildGridFromElements();
            repaint();
        }
        else{
            lastOperationOk = false;
            JOptionPane.showMessageDialog(null, "No se encontraron los elementos a intercambiar");
        }
    }
    
    /**
     * CICLO DOS - Tapa todas las tazas que tengan sus tapas correspondientes en la torre.
     * Empareja tazas con sus tapas asociadas.
     */
    public void cover(){
        int tapadasCount = 0;
        
        for(StackableElement e : elements){
            if(e.getType().equalsIgnoreCase("cup")){
                Cup cup = (Cup) e;
                if(!cup.hasLid()){
                    for(StackableElement l : elements){
                        if(l.getType().equalsIgnoreCase("lid") && l.getId() == e.getId()){
                            Lid lid = (Lid) l;
                            cup.setLid(lid);
                            tapadasCount++;
                            break;
                        }
                    }
                }
            }
        }
        
        lastOperationOk = true;
        repaint();
        if(tapadasCount > 0){
            JOptionPane.showMessageDialog(null, "Se taparon " + tapadasCount + " tazas");
        }
    }
    
    /**
     * CICLO DOS - Consulta un movimiento de intercambio que reduzca la altura total de la torre.
     * Retorna un array con los dos elementos que deberían intercambiarse.
     * 
     * @return Array bidimensional donde cada fila contiene [tipo, id] de un elemento a intercambiar
     */
    public String[][] swapToReduce(){
        String[][] result = new String[2][2];
        int bestHeightReduction = Integer.MIN_VALUE;
        int idx1 = -1, idx2 = -1;
        
        // Busca el par de elementos cuyo intercambio máximo reduce la altura
        for(int i = 0; i < elements.size(); i++){
            for(int j = i + 1; j < elements.size(); j++){
                // Simula el intercambio
                int heightBefore = towerHeightLevels();
                
                StackableElement temp = elements.get(i);
                elements.set(i, elements.get(j));
                elements.set(j, temp);
                rebuildGridFromElements();
                
                int heightAfter = towerHeightLevels();
                int reduction = heightBefore - heightAfter;
                
                if(reduction > bestHeightReduction){
                    bestHeightReduction = reduction;
                    idx1 = i;
                    idx2 = j;
                }
                
                // Revierte el intercambio
                elements.set(j, elements.get(i));
                elements.set(i, temp);
                rebuildGridFromElements();
            }
        }
        
        if(idx1 != -1 && idx2 != -1){
            result[0][0] = elements.get(idx1).getType();
            result[0][1] = String.valueOf(elements.get(idx1).getId());
            result[1][0] = elements.get(idx2).getType();
            result[1][1] = String.valueOf(elements.get(idx2).getId());
            
            lastOperationOk = true;
        }
        else{
            lastOperationOk = false;
        }
        
        repaint();
        return result;
    }
}
