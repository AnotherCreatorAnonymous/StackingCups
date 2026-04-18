package StackingCups;
import java.util.*;
import java.util.function.IntFunction;
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

    private static final String TYPE_CUP = "cup";
    private static final String TYPE_LID = "lid";
    private static final String TYPE_OPENER = "opener";
    private static final String TYPE_HIERARCHICAL = "hierarchical";
    private static final String TYPE_FEARFUL = "fearful";
    private static final String TYPE_CRAZY = "crazy";

    private final List<StackableElement> elements;
    private final Map<StackableElement, Integer> liftLevelsByElement;
    private boolean lastOperationOk;
    private final int width;
    private boolean isVisible;
    private final Map<String, IntFunction<StackableElement>> elementFactories;

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
        liftLevelsByElement = new HashMap<>();
        this.width = Math.max(1, width);
        this.cols = this.width;
        this.rows = Math.max(1, maxHeight);
        this.lastOperationOk = false;
        this.isVisible = false;
        this.elementFactories = new HashMap<>();

        grid = new StackableElement[rows][cols];
        registerBuiltInTypes();

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

    // --- CICLO 2 ---

    /**
     * Agrega una taza por identificador si respeta las reglas de negocio.
     *
     * @param n Identificador positivo de la taza.
     */
    public void pushCup(int n){
        addElement(TYPE_CUP, n);
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
        addElement(TYPE_LID, n);
    }

    // --- CICLO 4 ---

    /**
     * Agrega una taza especial opener por identificador.
     *
     * @param n Identificador positivo del elemento.
     */
    public void pushOpener(int n){
        addElement(TYPE_OPENER, n);
    }

    /**
     * Agrega una taza especial hierarchical por identificador.
     *
     * @param n Identificador positivo del elemento.
     */
    public void pushHierarchical(int n){
        addElement(TYPE_HIERARCHICAL, n);
    }

    /**
     * Agrega una tapa especial fearful por identificador.
     *
     * @param n Identificador positivo del elemento.
     */
    public void pushFearful(int n){
        addElement(TYPE_FEARFUL, n);
    }

    /**
     * Agrega una tapa especial crazy por identificador.
     *
     * @param n Identificador positivo del elemento.
     */
    public void pushCrazy(int n){
        addElement(TYPE_CRAZY, n);
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
        liftLevelsByElement.clear();
        
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
        liftLevelsByElement.clear();
        
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

        List<StackableElement> unit1 = elementUnit(elem1);
        List<StackableElement> unit2 = elementUnit(elem2);
        if(hasOverlap(unit1, unit2)){
            // Intercambiar dentro de la misma unidad no altera el estado final.
            lastOperationOk = true;
            repaint();
            return;
        }

        List<StackableElement> swapped = swapUnits(unit1, unit2);
        if(swapped.isEmpty()){
            fail("No fue posible construir el intercambio solicitado");
            return;
        }

        List<StackableElement> previous = new ArrayList<>(elements);
        elements.clear();
        elements.addAll(swapped);

        if(!rebuildGridFromElements()){
            elements.clear();
            elements.addAll(previous);
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
        liftLevelsByElement.clear();
        
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
     * Retorna el nivel base logico actual de un elemento por tipo e id.
     *
     * @param type Tipo del elemento buscado.
     * @param id Identificador del elemento buscado.
     * @return Nivel base en grilla o -1 si no existe.
     */
    public int logicalBaseLevel(String type, int id){
        StackableElement element = findElement(type, id);
        if(element == null){
            return -1;
        }

        int[] position = elementPositions.get(element);
        if(position == null){
            return -1;
        }

        return position[1];
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
            Cup cup = element.asCup();
            if(cup != null && cup.hasLid()){
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
        return Collections.unmodifiableList(elements);
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
        if(!isVisible){
            lastOperationOk = true;
            return;
        }

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
        liftLevelsByElement.clear();
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

        if(!lastOperationOk && isVisible){
            JOptionPane.showMessageDialog(null, "El simulador actual no soporta " + cups + " tazas");
        }
    }

    // --- CICLO 4 ---

    /**
     * Registra un nuevo tipo de elemento apilable para creacion extensible.
     *
     * @param type Nombre del tipo logico (por ejemplo cup, lid, opener).
     * @param factory Fabrica que crea el elemento a partir de su id.
     */
    public void registerType(String type, IntFunction<StackableElement> factory){
        if(type == null || type.trim().isEmpty() || factory == null){
            fail("No fue posible registrar el tipo solicitado");
            return;
        }

        elementFactories.put(type.toLowerCase(), factory);
        lastOperationOk = true;
    }

    /**
     * Registra los tipos base soportados por el simulador.
     */
    private void registerBuiltInTypes(){
        elementFactories.put(TYPE_CUP, Cup::new);
        elementFactories.put(TYPE_LID, Lid::new);
        elementFactories.put(TYPE_OPENER, OpenerCup::new);
        elementFactories.put(TYPE_HIERARCHICAL, HierarchicalCup::new);
        elementFactories.put(TYPE_FEARFUL, FearfulLid::new);
        elementFactories.put(TYPE_CRAZY, CrazyLid::new);
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

        if(!validateSpecialRequirements(element)){
            return;
        }

        List<StackableElement> previous = new ArrayList<>(elements);
        Map<StackableElement, Integer> previousLifts = new HashMap<>(liftLevelsByElement);

        applyPreInsertionEffects(element);
        insertElementRespectingBehavior(element);
        synchronizeCupLidLinks();

        if(!rebuildGridFromElements()){
            restoreElements(previous, previousLifts);
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
        IntFunction<StackableElement> factory = elementFactories.get(type.toLowerCase());
        if(factory == null){
            return null;
        }
        return factory.apply(id);
    }

    // --- CICLO 4 ---

    /**
     * Valida precondiciones especiales declaradas por el elemento.
     *
     * @param element Elemento por insertar.
     * @return true cuando las precondiciones se cumplen.
     */
    private boolean validateSpecialRequirements(StackableElement element){
        if(element.requiresCompanionCup() && findCompanionCupById(element.getId()) == null){
            fail("La tapa requiere que su taza companera ya exista en la torre");
            return false;
        }
        return true;
    }

    /**
     * Aplica efectos previos de insercion definidos por cada comportamiento.
     *
     * @param element Elemento que sera insertado.
     */
    private void applyPreInsertionEffects(StackableElement element){
        if(element.removesBlockingLids()){
            removeBlockingLidsFor(element);
        }
    }

    /**
     * Elimina solo tapas que bloquean el trayecto de entrada del opener.
     *
     * @param opener Elemento abridor por insertar.
     */
    private void removeBlockingLidsFor(StackableElement opener){
        Set<StackableElement> blockers = blockingLidsForBestPlacement(opener);
        if(blockers.isEmpty()){
            return;
        }

        elements.removeIf(blockers::contains);
        blockers.forEach(liftLevelsByElement::remove);
    }

    /**
     * Calcula las tapas bloqueantes del mejor nivel alcanzable.
     *
     * @param opener Elemento abridor por insertar.
     * @return Conjunto de tapas que impiden el paso en el mejor nivel.
     */
    private Set<StackableElement> blockingLidsForBestPlacement(StackableElement opener){
        int logicalWidth = opener.logicalWidth();
        int logicalHeight = opener.logicalHeight();
        int startX = startXForWidth(logicalWidth);

        if(startX < 0 || startX + logicalWidth > cols){
            return Collections.emptySet();
        }

        for(int level = rows - 1; level >= 0; level--){
            PlacementProbe probe = evaluatePlacementAllowingLidRemoval(level, startX, logicalWidth, logicalHeight);
            if(probe.reachable){
                return probe.blockingLids;
            }
        }

        return Collections.emptySet();
    }

    /**
     * Evalua un nivel y recolecta tapas removibles que bloquean el volumen.
     *
     * @param level Nivel base candidato.
     * @param startX Inicio horizontal del elemento.
     * @param logicalWidth Ancho logico del elemento.
     * @param logicalHeight Alto logico del elemento.
     * @return Resultado de alcanzabilidad y tapas bloqueantes en el nivel.
     */
    private PlacementProbe evaluatePlacementAllowingLidRemoval(int level, int startX, int logicalWidth, int logicalHeight){
        if(level < 0 || level >= rows){
            return PlacementProbe.unreachable();
        }

        int topLevel = level - logicalHeight + 1;
        if(topLevel < 0){
            return PlacementProbe.unreachable();
        }

        Set<StackableElement> blockers = new HashSet<>();

        for(int y = topLevel; y <= level; y++){
            for(int x = startX; x < startX + logicalWidth; x++){
                StackableElement occupied = grid[y][x];
                if(isUnremovableBlocker(occupied, logicalWidth, blockers)){
                    return PlacementProbe.unreachable();
                }
            }
        }

        return PlacementProbe.reachable(blockers);
    }

    /**
     * Evalua una celda ocupada para decidir si bloquea de forma removible.
     *
     * @param occupied Elemento ocupando la celda.
     * @param logicalWidth Ancho logico del elemento entrante.
     * @param blockers Coleccion de tapas bloqueantes removibles.
     * @return true cuando existe bloqueo no removible; false en otro caso.
     */
    private boolean isUnremovableBlocker(StackableElement occupied, int logicalWidth, Set<StackableElement> blockers){
        if(occupied == null || occupied.canContainLogicalWidth(logicalWidth)){
            return false;
        }

        if(occupied.getKind() == StackableElement.ElementKind.LID){
            blockers.add(occupied);
            return false;
        }

        return true;
    }

    /**
     * Estructura inmutable para evaluar alcanzabilidad de ubicacion.
     */
    private static final class PlacementProbe {
        private final boolean reachable;
        private final Set<StackableElement> blockingLids;

        private PlacementProbe(boolean reachable, Set<StackableElement> blockingLids){
            this.reachable = reachable;
            this.blockingLids = blockingLids;
        }

        private static PlacementProbe reachable(Set<StackableElement> blockingLids){
            return new PlacementProbe(true, blockingLids);
        }

        private static PlacementProbe unreachable(){
            return new PlacementProbe(false, Collections.emptySet());
        }
    }

    /**
     * Inserta el elemento respetando su estrategia polimorfica.
     *
     * @param element Elemento por insertar.
     */
    private void insertElementRespectingBehavior(StackableElement element){
        if(element.pushesSmallerElementsUp()){
            insertWithHierarchicalShift(element);
            return;
        }

        if(element.prefersBasePlacement()){
            insertAsCompanionBase(element);
            return;
        }

        elements.add(element);
    }

    /**
     * Inserta un elemento hierarchical por debajo de los mas pequenos.
     *
     * @param element Elemento hierarchical a insertar.
     */
    private void insertWithHierarchicalShift(StackableElement element){
        List<StackableElement> lowerOrEqual = new ArrayList<>();
        List<StackableElement> smaller = new ArrayList<>();

        for(StackableElement existing : elements){
            if(existing.logicalWidth() < element.logicalWidth()){
                smaller.add(existing);
                increaseLiftRequirement(existing, 1);
            }
            else{
                lowerOrEqual.add(existing);
            }
        }

        liftLevelsByElement.remove(element);

        elements.clear();
        elements.addAll(lowerOrEqual);
        elements.add(element);
        elements.addAll(smaller);
    }

    /**
     * Aumenta el desplazamiento vertical requerido para un elemento.
     *
     * @param element Elemento a desplazar.
     * @param levels Cantidad de niveles a incrementar.
     */
    private void increaseLiftRequirement(StackableElement element, int levels){
        int current = liftLevelsByElement.getOrDefault(element, 0);
        liftLevelsByElement.put(element, current + Math.max(0, levels));
    }

    /**
     * Inserta una tapa crazy como base de su taza companera.
     *
     * @param element Tapa con comportamiento de base.
     */
    private void insertAsCompanionBase(StackableElement element){
        int companionIndex = findCompanionCupIndex(element.getId());
        if(companionIndex < 0){
            elements.add(element);
            return;
        }
        elements.add(companionIndex, element);
    }

    /**
     * Restaura el estado previo de elementos y su representacion en grilla.
     *
     * @param previous Estado previo de la lista de elementos.
     */
    private void restoreElements(List<StackableElement> previous, Map<StackableElement, Integer> previousLifts){
        elements.clear();
        elements.addAll(previous);
        liftLevelsByElement.clear();
        liftLevelsByElement.putAll(previousLifts);
        synchronizeCupLidLinks();
        rebuildGridFromElements();
    }

    /**
     * Busca el indice de la taza companera por id.
     *
     * @param id Identificador companero.
     * @return Indice de la taza en la lista o -1 si no existe.
     */
    private int findCompanionCupIndex(int id){
        for(int i = 0; i < elements.size(); i++){
            StackableElement element = elements.get(i);
            if(element.getKind() == StackableElement.ElementKind.CUP && element.getId() == id){
                return i;
            }
        }
        return -1;
    }

    /**
     * Busca la taza companera de un elemento por id.
     *
     * @param id Identificador del companero.
     * @return Taza companera o null cuando no existe.
     */
    private StackableElement findCompanionCupById(int id){
        for(StackableElement element : elements){
            if(element.getKind() == StackableElement.ElementKind.CUP && element.getId() == id){
                return element;
            }
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

        List<StackableElement> removal = elementUnit(topElement);
        for(StackableElement element : removal){
            element.makeInvisible();
            elements.remove(element);
            liftLevelsByElement.remove(element);
        }

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

        List<StackableElement> removal = elementUnit(target);
        for(StackableElement element : removal){
            element.makeInvisible();
            elements.remove(element);
            liftLevelsByElement.remove(element);
        }

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
        Map<Integer, StackableElement> cups = new HashMap<>();
        Map<Integer, StackableElement> lids = new HashMap<>();

        for(StackableElement e : elements){
            if(e.getKind() == StackableElement.ElementKind.CUP){
                cups.put(e.getId(), e);
            }
            else if(e.getKind() == StackableElement.ElementKind.LID){
                lids.put(e.getId(), e);
            }
        }

        Set<Integer> allIds = new TreeSet<>(Collections.reverseOrder());
        allIds.addAll(cups.keySet());
        allIds.addAll(lids.keySet());

        List<StackableElement> ordered = new ArrayList<>();
        for(int id : allIds){
            StackableElement cup = cups.get(id);
            StackableElement lid = lids.get(id);

            if(cup != null){
                ordered.add(cup);
            }
            if(lid != null){
                ordered.add(lid);
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
            Cup cup = element.asCup();
            if(cup != null){
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
            Lid lid = element.asLid();
            if(lid != null){
                lidsById.put(lid.getId(), lid);
            }
        }

        for(StackableElement element : elements){
            Cup cup = element.asCup();
            if(cup != null){
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

        if(!descriptor[1].matches("\\d+")){
            return false;
        }

        String type = descriptor[0].toLowerCase();
        return type.equals(TYPE_CUP)
            || type.equals(TYPE_LID)
            || type.equals(TYPE_OPENER)
            || type.equals(TYPE_HIERARCHICAL)
            || type.equals(TYPE_FEARFUL)
            || type.equals(TYPE_CRAZY);
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
     * Retorna la unidad logica de movimiento de un elemento.
     *
     * Si el elemento es una taza cubierta, o su tapa asociada, la unidad
     * contiene ambos para preservar el movimiento conjunto.
     *
     * @param element Elemento base de consulta.
     * @return Lista en orden actual de la unidad a mover/eliminar.
     */
    private List<StackableElement> elementUnit(StackableElement element){
        if(element == null){
            return Collections.emptyList();
        }

        Set<StackableElement> unitSet = new HashSet<>();
        unitSet.add(element);

        if(element.hasLinkedLid()){
            Lid lid = element.getLinkedLid();
            if(lid != null){
                unitSet.add(lid);
            }
        }
        else if(element.getKind() == StackableElement.ElementKind.LID){
            StackableElement cup = findCompanionCupById(element.getId());
            if(cup != null){
                unitSet.add(cup);
            }
        }

        List<StackableElement> orderedUnit = new ArrayList<>();
        for(StackableElement candidate : elements){
            if(unitSet.contains(candidate)){
                orderedUnit.add(candidate);
            }
        }
        return orderedUnit;
    }

    /**
     * Determina si dos unidades de elementos comparten piezas.
     *
     * @param unit1 Primera unidad.
     * @param unit2 Segunda unidad.
     * @return true si comparten al menos un elemento; false en caso contrario.
     */
    private boolean hasOverlap(List<StackableElement> unit1, List<StackableElement> unit2){
        Set<StackableElement> set = new HashSet<>(unit1);
        for(StackableElement element : unit2){
            if(set.contains(element)){
                return true;
            }
        }
        return false;
    }

    /**
     * Intercambia dos unidades logicas conservando orden interno de cada una.
     *
     * @param unit1 Primera unidad.
     * @param unit2 Segunda unidad.
     * @return Nueva lista resultante del intercambio.
     */
    private List<StackableElement> swapUnits(List<StackableElement> unit1, List<StackableElement> unit2){
        if(unit1.isEmpty() || unit2.isEmpty()){
            return Collections.emptyList();
        }

        Set<StackableElement> set1 = new HashSet<>(unit1);
        Set<StackableElement> set2 = new HashSet<>(unit2);

        List<StackableElement> swapped = new ArrayList<>();
        boolean inserted1 = false;
        boolean inserted2 = false;

        for(StackableElement element : elements){
            boolean belongsToUnit1 = set1.contains(element);
            boolean belongsToUnit2 = set2.contains(element);

            if(belongsToUnit1){
                if(!inserted1){
                    swapped.addAll(unit2);
                    inserted1 = true;
                }
            }
            else if(belongsToUnit2){
                if(!inserted2){
                    swapped.addAll(unit1);
                    inserted2 = true;
                }
            }
            else{
                swapped.add(element);
            }
        }

        return swapped;
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
            int logicalHeight = element.logicalHeight();
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
                if(occupied != null && !canShareCell(occupied, logicalWidth, y)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determina si una celda ocupada puede compartirse por anidamiento.
     *
     * Regla adicional: una taza anidada no puede ocupar la fila base de
     * la taza contenedora, evitando solapamiento visual en la base.
     *
     * @param occupied Elemento ya presente en la celda.
     * @param incomingLogicalWidth Ancho logico del elemento entrante.
     * @param row Fila de grilla evaluada.
     * @return true si la celda puede compartirse; false si hay colision.
     */
    private boolean canShareCell(StackableElement occupied, int incomingLogicalWidth, int row){
        if(!canNestInsideCup(occupied, incomingLogicalWidth)){
            return false;
        }

        int[] occupiedPos = elementPositions.get(occupied);
        if(occupiedPos == null){
            return true;
        }

        int occupiedBaseLevel = occupiedPos[1];
        return row < occupiedBaseLevel;
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
        return occupied.canContainLogicalWidth(currentLogicalWidth);
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
        int logicalHeight = element.logicalHeight();
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
        pruneLiftRequirements();
        clearGrid();

        int maxBaseLevelAllowed = rows - 1;

        for(StackableElement element : elements){
            int placedLevel = placeSingleElement(element, maxBaseLevelAllowed);
            if(placedLevel == -1){
                return false;
            }

            maxBaseLevelAllowed = placedLevel - 1;
        }

        return true;
    }

    /**
     * Elimina requisitos de elevacion de elementos ya ausentes.
     */
    private void pruneLiftRequirements(){
        liftLevelsByElement.keySet().removeIf(element -> !elements.contains(element));
    }

    /**
     * Ubica un elemento individual en el nivel mas bajo valido.
     *
     * @param element Elemento a ubicar.
     * @return true si el elemento fue ubicado; false si no hay espacio.
     */
    private int placeSingleElement(StackableElement element, int maxBaseLevelAllowed){
        int logicalWidth = element.logicalWidth();
        int logicalHeight = element.logicalHeight();
        int settledLevel = findLevelForWidth(logicalWidth, logicalHeight);
        if(settledLevel == -1){
            return -1;
        }

        int requiredLift = liftLevelsByElement.getOrDefault(element, 0);
        int level = findConstrainedLevel(
            logicalWidth,
            logicalHeight,
            settledLevel,
            requiredLift,
            maxBaseLevelAllowed
        );

        if(level == -1){
            return -1;
        }

        placeAtLevel(element, level, logicalWidth);
        return level;
    }

    /**
     * Busca un nivel valido que respete restricciones de elevacion y orden.
     *
     * @param logicalWidth Ancho logico del elemento.
     * @param logicalHeight Alto logico del elemento.
     * @param settledLevel Nivel de equilibrio por gravedad.
     * @param requiredLift Niveles minimos que debe subir el elemento.
     * @param maxBaseLevelAllowed Nivel base maximo permitido por orden.
     * @return Nivel valido o -1 si no existe.
     */
    private int findConstrainedLevel(
        int logicalWidth,
        int logicalHeight,
        int settledLevel,
        int requiredLift,
        int maxBaseLevelAllowed
    ){
        int maxAllowedByLift = settledLevel - Math.max(0, requiredLift);
        int searchStart = Math.min(settledLevel, Math.min(maxBaseLevelAllowed, maxAllowedByLift));
        if(searchStart < 0){
            return -1;
        }

        int startX = startXForWidth(logicalWidth);
        for(int level = searchStart; level >= 0; level--){
            if(canPlace(level, startX, logicalWidth, logicalHeight)){
                return level;
            }
        }

        return -1;
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
     * Busca el ultimo elemento agregado de un tipo en la lista actual.
     *
     * @param type Tipo de elemento a buscar (cup o lid).
     * @return Elemento encontrado o null si no existe.
     */
    private StackableElement findTopElementByType(String type){
        for(int i = elements.size() - 1; i >= 0; i--){
            StackableElement element = elements.get(i);
            if(element.getType().equalsIgnoreCase(type)){
                return element;
            }
        }

        return null;
    }
}
