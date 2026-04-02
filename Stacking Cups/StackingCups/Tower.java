package StackingCups;
import java.util.*;
import javax.swing.*;
import shapes.*;

/**
 * Simula visualmente una torre de elementos apilables (tazas y tapas).
 *
 * La clase administra el estado interno de la torre, sus reglas de apilado
 * y la representacion grafica usando el paquete shapes.
 *
 * @author Carlos Felipe Jimenez Sposito
 * @version 2.0
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
    private final int sceneWidth;
    private final int sceneHeight;
    
    /**
     * OPTIMIZACION: Cachea las posiciones espaciales logicas [startX, level]
     * de cada elemento para evitar recorrer la matriz grid en O(rows x cols).
     * Se actualiza en placeAtLevel() y se limpia en clearGrid().
     */
    private final Map<StackableElement, int[]> elementPositions = new HashMap<>();

    /**
     * Crea una torre vacia por dimensiones logicas.
     *
     * @param width Ancho maximo en celdas logicas de 1 cm.
     * @param maxHeight Altura maxima en niveles logicos de 1 cm.
     */
    public Tower(int width, int maxHeight) {
        elements = new ArrayList<>();
        this.width = Math.max(1, width);
        this.cols = this.width;
        this.rows = Math.max(1, maxHeight);
        this.lastOperationOk = false;
        this.isVisible = false;

        grid = new StackableElement[rows][cols];

        int towerPixelWidth = cols * CELL_SIZE;
        int meterPixelWidth = METER_BAR_WIDTH + 12;
        sceneWidth = towerPixelWidth + METER_GAP + meterPixelWidth;
        sceneHeight = rows * CELL_SIZE + BASE_THICKNESS;

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
     * Crea un simulador vacio dimensionado por cantidad objetivo de tazas.
     *
     * @param cups Cantidad objetivo usada para calcular ancho y altura inicial.
     */
    public Tower(int cups){
        this(Math.max(1, (2 * Math.max(1, cups)) - 1), Math.max(2, Math.max(1, cups) * 2));
    }

    /**
     * Agrega una taza por identificador si respeta las reglas de negocio.
     *
     * @param n Identificador positivo de la taza.
     */
    public void pushCup(int n){
        addElement("cup", n);
    }

    /**
     * Elimina la ultima taza agregada.
     */
    public void popCup(){
        removeLastByType("cup");
    }

    /**
     * Elimina una taza especifica por identificador.
     *
     * @param n Identificador de la taza a eliminar.
     */
    public void removeCup(int n){
        removeByTypeAndId("cup", n);
    }

    /**
     * Agrega una tapa por identificador si respeta las reglas de negocio.
     *
     * @param n Identificador positivo de la tapa.
     */
    public void pushLid(int n) {
        addElement("lid", n);
    }

    /**
     * Elimina la ultima tapa agregada.
     */
    public void popLid(){
        removeLastByType("lid");
    }

    /**
     * Elimina una tapa especifica por identificador.
     *
     * @param n Identificador de la tapa a eliminar.
     */
    public void removeLid(int n){
        removeByTypeAndId("lid", n);
    }

    /**
     * Ordena la torre de mayor a menor base y pone la tapa encima de su taza.
     * OPTIMIZACION: No sincroniza links aqui; solo reordena la lista.
     */
    public void orderTower(){
        List<StackableElement> ordered = buildOrderedByBase();
        elements.clear();
        elements.addAll(ordered);
        
        if(!rebuildGridFromElements()){
            fail("No fue posible ordenar toda la torre");
            return;
        }
        
        lastOperationOk = true;
        repaint();
    }

    /**
     * Invierte el orden actual de la lista de elementos.
     * OPTIMIZACION: No sincroniza links aqui; solo reordena la lista.
     */
    public void reverseTower(){
        List<StackableElement> reversed = new ArrayList<>(elements);
        Collections.reverse(reversed);
        elements.clear();
        elements.addAll(reversed);
        
        if(!rebuildGridFromElements()){
            fail("No fue posible invertir toda la torre");
            return;
        }
        
        lastOperationOk = true;
        repaint();
    }

    /**
     * Intercambia dos elementos identificados por tipo e id.
     * OPTIMIZACION: No sincroniza links aqui; solo reordena la lista.
     *
     * @param o1 Descriptor del primer elemento con formato [tipo, id].
     * @param o2 Descriptor del segundo elemento con formato [tipo, id].
     */
    public void swap(String[] o1, String[] o2){
        if(!isValidDescriptor(o1) || !isValidDescriptor(o2)){
            fail("Los datos para intercambiar son invalidos");
            return;
        }

        StackableElement elem1 = findElement(o1[0], Integer.parseInt(o1[1]));
        StackableElement elem2 = findElement(o2[0], Integer.parseInt(o2[1]));

        if(elem1 == null || elem2 == null){
            fail("No se encontraron los elementos a intercambiar");
            return;
        }

        int idx1 = elements.indexOf(elem1);
        int idx2 = elements.indexOf(elem2);
        Collections.swap(elements, idx1, idx2);
        
        if(!rebuildGridFromElements()){
            Collections.swap(elements, idx1, idx2);
            rebuildGridFromElements();
            fail("No fue posible aplicar el intercambio");
            return;
        }
        
        lastOperationOk = true;
        repaint();
    }

    /**
     * Empareja tapas y tazas del mismo numero para que se dibujen juntas.
     * OPTIMIZACION: Solo reordena la lista, sin resincronizar enlaces.
     */
    public void cover(){
        synchronizeCupLidLinks();

        int covered = lidedCups().length;
        if(covered == 0){
            fail("No hay tazas con tapa para cubrir");
            return;
        }

        List<StackableElement> coveredOrder = buildCoveredOrder();
        elements.clear();
        elements.addAll(coveredOrder);
        
        if(!rebuildGridFromElements()){
            fail("No fue posible cubrir toda la torre");
            return;
        }
        
        lastOperationOk = true;
        repaint();
    }

    /**
     * Busca el intercambio que mayor reduccion de altura produce.
     * OPTIMIZACION: Desactiva temporalmente el renderizado durante la simulacion de fuerza bruta.
     * Sincroniza links una sola vez antes del bucle y lo deja activo para las evaluaciones.
     *
     * @return Matriz de 2x2 con [tipo,id] de los elementos a intercambiar.
     */
    public String[][] swapToReduce(){
        String[][] result = new String[2][2];

        if(elements.size() < 2){
            lastOperationOk = false;
            return result;
        }

        // Desactivar renderizado durante simulacion de fuerza bruta
        boolean wasVisible = isVisible;
        isVisible = false;

        // Sincronizar links una sola vez antes del bucle
        synchronizeCupLidLinks();
        
        int baseHeight = towerHeightLevels();
        int bestReduction = 0;
        int bestI = -1;
        int bestJ = -1;

        for(int i = 0; i < elements.size(); i++){
            for(int j = i + 1; j < elements.size(); j++){
                Collections.swap(elements, i, j);
                boolean valid = rebuildGridFromElements();

                if(valid){
                    int newHeight = towerHeightLevels();
                    int reduction = baseHeight - newHeight;
                    if(reduction > bestReduction){
                        bestReduction = reduction;
                        bestI = i;
                        bestJ = j;
                    }
                }

                Collections.swap(elements, i, j);
                rebuildGridFromElements();
            }
        }

        // Restaurar visibilidad
        isVisible = wasVisible;
        rebuildGridFromElements();

        if(bestI == -1 || bestJ == -1){
            lastOperationOk = false;
            return result;
        }

        StackableElement e1 = elements.get(bestI);
        StackableElement e2 = elements.get(bestJ);
        result[0][0] = e1.getType();
        result[0][1] = String.valueOf(e1.getId());
        result[1][0] = e2.getType();
        result[1][1] = String.valueOf(e2.getId());

        lastOperationOk = true;
        if(wasVisible) repaint();
        return result;
    }

    /**
     * Retorna la cantidad de elementos registrados en la torre.
     *
     * @return Numero de elementos (tazas y tapas) almacenados.
     */
    public int height() {
        return elements.size();
    }

    /**
     * Retorna el resultado de la ultima operacion ejecutada.
     *
     * @return true si la ultima operacion fue exitosa; false en caso contrario.
     */
    public boolean ok(){
        return lastOperationOk;
    }

    /**
     * Retorna los elementos de base a cima en formato [tipo,id].
     *
     * @return Matriz con el estado de la torre en minusculas y orden fisico.
     */
    public String[][] stackingItems() {
        List<StackableElement> ordered = gridElementsFromBaseToTop();
        String[][] result = new String[ordered.size()][2];

        for (int i = 0; i < ordered.size(); i++) {
            StackableElement e = ordered.get(i);
            result[i][0] = e.getType().toLowerCase();
            result[i][1] = String.valueOf(e.getId());
        }

        return result;
    }

    /**
     * Retorna los ids de tazas cubiertas, ordenados de menor a mayor.
     *
     * @return Arreglo ascendente con ids de tazas que tienen tapa asociada.
     */
    public int[] lidedCups(){
        List<Integer> ids = new ArrayList<>();
        synchronizeCupLidLinks();

        for(StackableElement element : elements){
            if(element instanceof Cup cup && cup.hasLid()){
                ids.add(cup.getId());
            }
        }

        Collections.sort(ids);

        int[] result = new int[ids.size()];
        for(int i = 0; i < ids.size(); i++){
            result[i] = ids.get(i);
        }
        return result;
    }

    /**
     * Retorna la lista interna de elementos de la torre.
     *
     * @return Lista mutable de elementos del estado actual.
     */
    public List<StackableElement> getElements() {
        return elements;
    }

    /**
     * Vuelve visible la torre si su escena cabe dentro del canvas.
     */
    public void makeVisible(){
        if(sceneWidth > Canvas.CANVAS_WIDTH || sceneHeight > Canvas.CANVAS_HEIGHT){
            fail("La torre excede el tamano visible del canvas");
            return;
        }

        isVisible = true;
        repaint();
        lastOperationOk = true;
    }

    /**
     * Oculta la torre y limpia su representacion en canvas.
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
     * Cierra el simulador con confirmacion de usuario.
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
     * Metodo legado para configurar el simulador y dejar la torre vacia.
     *
     * @param cups Cantidad objetivo de tazas para validar capacidad actual.
     */
    public void tower(int cups){
        for(StackableElement element : elements){
            element.makeInvisible();
        }

        elements.clear();
        synchronizeCupLidLinks();
        clearGrid();
        repaint();

        if(cups < 0){
            fail("La cantidad de tazas no puede ser negativa");
            return;
        }

        boolean fitsWidth = (2 * Math.max(1, cups)) - 1 <= width;
        boolean fitsHeight = Math.max(1, cups) <= rows;
        lastOperationOk = fitsWidth && fitsHeight;

        if(!lastOperationOk){
            JOptionPane.showMessageDialog(null, "El simulador actual no soporta " + cups + " tazas");
        }
    }

    /**
     * Agrega un elemento por tipo respetando validaciones de dominio.
     *
     * @param type Tipo de elemento: cup o lid.
     * @param n Identificador del elemento.
     */
    private void addElement(String type, int n){
        if(!validatePositiveId(n)){
            return;
        }
        if(!validateWidthForId(n)){
            return;
        }
        if(existsTypeWithId(type, n)){
            fail("Ya existe un elemento de tipo " + type + " con id " + n);
            return;
        }

        StackableElement element = createElement(type, n);
        if(element == null){
            fail("Tipo de elemento invalido");
            return;
        }

        elements.add(element);
        synchronizeCupLidLinks();

        if(!rebuildGridFromElements()){
            elements.remove(element);
            synchronizeCupLidLinks();
            rebuildGridFromElements();
            fail("No hay espacio para el elemento solicitado");
            return;
        }

        lastOperationOk = true;
        repaint();
    }

    /**
     * Crea una instancia de elemento por tipo.
     *
     * @param type Tipo de elemento a crear.
     * @param id Identificador del elemento.
     * @return Instancia creada o null si el tipo no existe.
     */
    private StackableElement createElement(String type, int id){
        if("cup".equalsIgnoreCase(type)){
            return new Cup(id);
        }
        if("lid".equalsIgnoreCase(type)){
            return new Lid(id);
        }
        return null;
    }

    /**
     * Elimina el elemento mas alto encontrado del tipo solicitado.
     *
     * @param type Tipo de elemento a eliminar.
     */
    private void removeLastByType(String type){
        StackableElement topElement = findTopElementByType(type);
        if(topElement == null){
            fail("No se encontro un elemento tipo: " + type);
            return;
        }

        topElement.makeInvisible();
        elements.remove(topElement);
        synchronizeCupLidLinks();
        rebuildGridFromElements();
        lastOperationOk = true;
        repaint();
    }

    /**
     * Elimina un elemento por tipo e identificador.
     *
     * @param type Tipo del elemento.
     * @param id Identificador a eliminar.
     */
    private void removeByTypeAndId(String type, int id){
        StackableElement target = findElement(type, id);
        if(target == null){
            fail("No se encontro el elemento a eliminar");
            return;
        }

        target.makeInvisible();
        elements.remove(target);
        synchronizeCupLidLinks();
        rebuildGridFromElements();
        lastOperationOk = true;
        repaint();
    }

    /**
     * Construye un orden de mayor a menor base y tapa sobre taza del mismo id.
     *
     * @return Lista ordenada lista para reconstruir la torre.
     */
    private List<StackableElement> buildOrderedByBase(){
        Map<Integer, Cup> cups = new HashMap<>();
        Map<Integer, Lid> lids = new HashMap<>();

        for(StackableElement e : elements){
            if(e instanceof Cup cup){
                cups.put(e.getId(), cup);
            }
            else if(e instanceof Lid lid){
                lids.put(e.getId(), lid);
            }
        }

        Set<Integer> allIds = new TreeSet<>(Collections.reverseOrder());
        allIds.addAll(cups.keySet());
        allIds.addAll(lids.keySet());

        List<StackableElement> ordered = new ArrayList<>();
        for(int id : allIds){
            Cup cup = cups.get(id);
            Lid lid = lids.get(id);

            if(lid != null){
                ordered.add(lid);
            }
            if(cup != null){
                ordered.add(cup);
            }
        }

        return ordered;
    }

    /**
     * Construye un orden que prioriza taza seguida por su tapa asociada.
     *
     * @return Lista con pares taza-tapa ya acoplados.
     */
    private List<StackableElement> buildCoveredOrder(){
        List<StackableElement> coveredOrder = new ArrayList<>();
        Set<StackableElement> added = new HashSet<>();

        addCupsAndLinkedLids(coveredOrder, added);
        addRemainingElements(coveredOrder, added);

        return coveredOrder;
    }

    /**
     * Agrega cada taza y su tapa enlazada al orden de cobertura.
     *
     * @param coveredOrder Lista destino del nuevo orden.
     * @param added Conjunto de elementos ya agregados.
     */
    private void addCupsAndLinkedLids(List<StackableElement> coveredOrder, Set<StackableElement> added){
        for(StackableElement element : elements){
            if(element instanceof Cup cup){
                addIfMissing(cup, coveredOrder, added);
                if(cup.hasLid()){
                    Lid lid = cup.getLid();
                    if(lid != null){
                        addIfMissing(lid, coveredOrder, added);
                    }
                }
            }
        }
    }

    /**
     * Agrega al final los elementos que no quedaron en la primera pasada.
     *
     * @param coveredOrder Lista destino del nuevo orden.
     * @param added Conjunto de elementos ya agregados.
     */
    private void addRemainingElements(List<StackableElement> coveredOrder, Set<StackableElement> added){
        for(StackableElement element : elements){
            addIfMissing(element, coveredOrder, added);
        }
    }

    /**
     * Agrega un elemento si aun no fue agregado al orden destino.
     *
     * @param element Elemento candidato a agregar.
     * @param target Lista destino.
     * @param added Conjunto de control de duplicados.
     */
    private void addIfMissing(StackableElement element, List<StackableElement> target, Set<StackableElement> added){
        if(!added.contains(element)){
            target.add(element);
            added.add(element);
        }
    }

    /**
     * Sincroniza el enlace 1 a 1 entre tazas y tapas del mismo id.
     */
    private void synchronizeCupLidLinks(){
        Map<Integer, Lid> lidsById = new HashMap<>();

        for(StackableElement element : elements){
            if(element instanceof Lid lid){
                lidsById.put(element.getId(), lid);
            }
        }

        for(StackableElement element : elements){
            if(element instanceof Cup cup){
                cup.setLid(lidsById.get(cup.getId()));
            }
        }
    }

    /**
     * Busca un elemento por tipo e id.
     *
     * @param type Tipo esperado del elemento.
     * @param id Identificador del elemento.
     * @return Elemento encontrado o null si no existe.
     */
    private StackableElement findElement(String type, int id){
        for(StackableElement element : elements){
            if(element.getType().equalsIgnoreCase(type) && element.getId() == id){
                return element;
            }
        }
        return null;
    }

    /**
     * Valida un descriptor de intercambio [tipo,id].
     *
     * @param descriptor Arreglo esperado con dos posiciones.
     * @return true si el descriptor es valido; false en caso contrario.
     */
    private boolean isValidDescriptor(String[] descriptor){
        if(descriptor == null || descriptor.length != 2){
            return false;
        }

        if(descriptor[0] == null || descriptor[1] == null){
            return false;
        }

        try{
            Integer.parseInt(descriptor[1]);
        }
        catch(NumberFormatException ignored){
            return false;
        }

        return descriptor[0].equalsIgnoreCase("cup") || descriptor[0].equalsIgnoreCase("lid");
    }

    /**
     * Valida que el id sea positivo.
     *
     * @param n Identificador a validar.
     * @return true si el id es valido; false en caso contrario.
     */
    private boolean validatePositiveId(int n){
        if(n <= 0){
            fail("El identificador debe ser mayor que cero");
            return false;
        }
        return true;
    }

    /**
     * Valida que el ancho del elemento por id quepa en la torre.
     *
     * @param n Identificador usado para calcular el ancho logico.
     * @return true si cabe; false en caso contrario.
     */
    private boolean validateWidthForId(int n){
        int logicalWidth = (2 * n) - 1;
        if(logicalWidth > width){
            fail("El elemento excede el ancho de la torre");
            return false;
        }
        return true;
    }

    /**
     * Indica si ya existe un elemento del mismo tipo e id.
     *
     * @param type Tipo a comparar.
     * @param id Identificador a comparar.
     * @return true si ya existe una coincidencia; false si no existe.
     */
    private boolean existsTypeWithId(String type, int id){
        return findElement(type, id) != null;
    }

    /**
     * Marca una operacion como fallida y notifica al usuario si aplica.
     *
     * @param message Mensaje descriptivo del fallo.
     */
    private void fail(String message){
        lastOperationOk = false;
        if(isVisible){
            JOptionPane.showMessageDialog(null, message);
        }
    }

    /**
     * Redibuja todos los elementos visibles segun su posicion actual.
     * OPTIMIZACION: Itera sobre elements en O(N) en lugar de O(rows x cols),
     * obteniendo coordenadas directamente del cache elementPositions.
     */
    private void repaint(){
        if(!isVisible){
            return;
        }

        for(StackableElement e : elements){
            e.erase();
        }

        // OPTIMIZACION: Usar cache de posiciones en lugar de buscar en grid
        for(StackableElement element : elements){
            int[] pos = elementPositions.get(element);
            if(pos != null){
                int startX = pos[0];
                int level = pos[1];
                int pixelX = originX + (startX * CELL_SIZE);
                int pixelY = levelToPixelY(level, element.getHeight());
                element.draw(pixelX, pixelY);
            }
        }

        drawBase();
        drawHeightMeter();
    }

    /**
     * Dibuja la base de referencia de la torre.
     */
    private void drawBase(){
        baseLine.changePosition(originX, baseY);
        baseLine.makeVisible();

        baseMarker.changePosition(meterX - 6, baseY - (CELL_SIZE / 2));
        baseMarker.makeVisible();
    }

    /**
     * Dibuja el medidor lateral de altura sin valores de texto.
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
     * Convierte un nivel logico de grilla a coordenada Y en pixeles.
     *
     * @param level Nivel logico en grilla.
     * @param elementHeight Altura del elemento en pixeles.
     * @return Coordenada Y de dibujo para el elemento.
     */
    private int levelToPixelY(int level, int elementHeight){
        int levelsFromBase = (rows - 1) - level;
        return baseY - elementHeight - (levelsFromBase * CELL_SIZE);
    }

    /**
     * Calcula la altura fisica actual de la torre en niveles logicos.
     * OPTIMIZACION: Evalua el nivel minimo en elementPositions en O(N)
     * en lugar de buscar en toda la grid en O(rows x cols).
     *
     * @return Altura ocupada por la torre en niveles de la grilla.
     */
    private int towerHeightLevels(){
        if(elementPositions.isEmpty()){
            return 0;
        }
        
        int minLevel = Integer.MAX_VALUE;
        for(Map.Entry<StackableElement, int[]> entry : elementPositions.entrySet()){
            StackableElement element = entry.getKey();
            int[] pos = entry.getValue();
            int baseLevel = pos[1];
            int logicalHeight = Math.max(1, element.getHeight() / CELL_SIZE);
            int topLevel = baseLevel - logicalHeight + 1;
            minLevel = Math.min(minLevel, topLevel);
        }
        
        return minLevel == Integer.MAX_VALUE ? 0 : rows - minLevel;
    }

    /**
     * Busca el nivel mas bajo valido para un ancho logico dado.
     * Ahora permite "voladizos" (poner grandes sobre pequeñas) omitiendo la validacion de soporte.
     *
     * @param logicalWidth Ancho logico del elemento en celdas.
        * @param logicalHeight Altura logica del elemento en celdas.
     * @return Nivel encontrado o -1 si no existe un nivel valido (supera altura maxima).
     */
    private int findLevelForWidth(int logicalWidth, int logicalHeight){
        int startX = startXForWidth(logicalWidth);

        // Si la pieza de por sí es más ancha que el Canvas lógico, falla.
        if(startX < 0 || startX + logicalWidth > cols){
            return -1;
        }

        // Simulamos gravedad: probamos desde el piso (rows - 1) hacia arriba (0)
        for(int level = rows - 1; level >= 0; level--){
            if(canPlace(level, startX, logicalWidth, logicalHeight)){
                // En el momento en que cabe sin superponerse con otra pieza, ahí se queda
                return level; 
            }
        }
        
        // Si recorrió toda la torre y no encontró espacio, superó la altura máxima
        return -1; 
    }

    /**
     * Retorna el inicio horizontal centrado para un ancho logico.
     *
     * @param logicalWidth Ancho en celdas del elemento.
     * @return Posicion X inicial para ubicar el elemento centrado.
     */
    private int startXForWidth(int logicalWidth){
        return (cols - logicalWidth) / 2;
    }

    /**
     * Verifica si una franja horizontal esta libre en un nivel de grilla.
     *
     * @param level Nivel de la grilla.
     * @param startX Posicion inicial horizontal.
     * @param logicalWidth Ancho en celdas.
     * @param logicalHeight Altura en celdas del elemento.
     * @return true si se puede ubicar; false si hay colision.
     */
    private boolean canPlace(int level, int startX, int logicalWidth, int logicalHeight){
        if(level < 0 || level >= rows){
            return false;
        }

        int topLevel = level - logicalHeight + 1;
        if(topLevel < 0){
            return false;
        }

        for(int y = topLevel; y <= level; y++){
            for(int x = startX; x < startX + logicalWidth; x++){
                StackableElement occupied = grid[y][x];
                if(occupied != null && !canNestInsideCup(occupied, logicalWidth)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Regla de encaje: una Cup o Lid puede encajar solo si es mas pequena
     * que la Cup ocupante sobre la que cae.
     *
     * @param occupied Elemento ya presente en la celda objetivo.
     * @param currentLogicalWidth Ancho logico de la pieza a ubicar.
     * @return true si el encaje es valido; false si debe considerarse colision.
     */
    private boolean canNestInsideCup(StackableElement occupied, int currentLogicalWidth){
        if(!(occupied instanceof Cup)){
            return false;
        }

        int occupiedLogicalWidth = occupied.getWidth() / CELL_SIZE;
        return currentLogicalWidth < occupiedLogicalWidth;
    }

    /**
     * Ubica un elemento en un nivel especifico de la grilla.
     * OPTIMIZACION: Cachea la posicion logica [startX, level] en elementPositions.
     *
     * @param element Elemento a ubicar.
     * @param level Nivel destino.
     * @param logicalWidth Ancho logico en celdas.
     */
    private void placeAtLevel(StackableElement element, int level, int logicalWidth){
        int startX = startXForWidth(logicalWidth);
        int logicalHeight = Math.max(1, element.getHeight() / CELL_SIZE);
        int topLevel = level - logicalHeight + 1;

        for(int y = topLevel; y <= level; y++){
            for(int x = startX; x < startX + logicalWidth; x++){
                grid[y][x] = element;
            }
        }
        
        // OPTIMIZACION: Guardar posicion en cache para repaint O(N)
        elementPositions.put(element, new int[]{startX, level});
    }

    /**
     * Reconstruye la grilla completa desde la lista de elementos.
     *
     * @return true si toda la reconstruccion fue exitosa; false si falla.
     */
    private boolean rebuildGridFromElements(){
        clearGrid();

        for(StackableElement element : elements){
            if(!placeSingleElement(element)){
                return false;
            }
        }

        return true;
    }

    /**
     * Ubica un elemento individual en el nivel mas bajo valido.
     *
     * @param element Elemento a ubicar.
     * @return true si el elemento fue ubicado; false si no hay espacio.
     */
    private boolean placeSingleElement(StackableElement element){
        int logicalWidth = element.getWidth() / CELL_SIZE;
        int logicalHeight = Math.max(1, element.getHeight() / CELL_SIZE);
        int level = findLevelForWidth(logicalWidth, logicalHeight);
        if(level == -1){
            return false;
        }
        placeAtLevel(element, level, logicalWidth);
        return true;
    }

    /**
     * Limpia por completo la grilla de ocupacion y el cache de posiciones.
     * OPTIMIZACION: Tambien limpia el mapa elementPositions para mantener coherencia.
     */
    private void clearGrid(){
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < cols; x++){
                grid[y][x] = null;
            }
        }
        
        // OPTIMIZACION: Limpiar cache de posiciones
        elementPositions.clear();
    }

    /**
     * Retorna los elementos segun su posicion fisica de base a cima.
     *
     * @return Lista sin repetidos en el orden fisico actual.
     */
    private List<StackableElement> gridElementsFromBaseToTop(){
        List<StackableElement> ordered = new ArrayList<>();
        Set<StackableElement> seen = new HashSet<>();

        for(int y = rows - 1; y >= 0; y--){
            for(int x = 0; x < cols; x++){
                StackableElement element = grid[y][x];
                if(element != null && !seen.contains(element)){
                    ordered.add(element);
                    seen.add(element);
                }
            }
        }

        return ordered;
    }

    /**
     * Busca el elemento mas alto de un tipo en la grilla actual.
     *
     * @param type Tipo de elemento a buscar (cup o lid).
     * @return Elemento encontrado en la mayor altura o null si no existe.
     */
    private StackableElement findTopElementByType(String type){
        Set<StackableElement> seen = new HashSet<>();

        for(int y = 0; y < rows; y++){
            for(int x = 0; x < cols; x++){
                StackableElement element = grid[y][x];
                if(element != null && !seen.contains(element)){
                    seen.add(element);
                    if(element.getType().equalsIgnoreCase(type)){
                        return element;
                    }
                }
            }
        }

        return null;
    }
}
