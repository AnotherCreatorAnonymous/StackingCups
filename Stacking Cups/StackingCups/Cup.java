package StackingCups;
import shapes.*;

 
/**
 * Modela una taza apilable con representacion visual hueca.
 *
 * El tamano de la taza se deriva de su identificador con base impar,
 * siguiendo las reglas del problema de Stacking Cups.
 * 
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */

public class Cup extends StackableElement {

    private static final String[] CUP_COLORS = {
        "red", "blue", "green", "yellow", "orange", "cyan", "magenta", "purple",
        "teal", "gold", "salmon", "indigo", "turquoise", "lime", "orchid", "crimson",
        "coral", "khaki", "lavender", "steelBlue", "tomato", "deepPink", "sienna", "plum"
    };
    private static final int WALL_THICKNESS = StackableElement.UNIT_PIXELS;

    private Lid lid;
    private final Rectangle cupShape;
    private final Rectangle cupInnerShape;

    /**
     * Construye una copa a partir de su id y define su tamano visual.
     *
     * @param n Identificador positivo de la copa.
     */
    public Cup(int n){
        
        id = n;
        width = (2*n - 1) * StackableElement.UNIT_PIXELS;
        height = (2*n - 1) * StackableElement.UNIT_PIXELS;
        
        cupShape = new Rectangle();
        cupShape.changeSize(height, width);
        cupShape.changeColor(colorForId(n));

        cupInnerShape = new Rectangle();
        cupInnerShape.changeSize(innerHeight(height), innerWidth(width));
        cupInnerShape.changeColor("white");
        
    }

    /**
     * Retorna el ancho interior visible de la copa.
     *
     * @param outerWidth Ancho exterior de la taza.
     * @return Ancho interior resultante para el hueco visual.
     */
    private int innerWidth(int outerWidth){
        return Math.max(1, outerWidth - 2 * WALL_THICKNESS);
    }

    /**
     * Retorna la altura interior visible de la copa.
     *
     * @param outerHeight Altura exterior de la taza.
     * @return Altura interior resultante para el hueco visual.
     */
    private int innerHeight(int outerHeight){
        return Math.max(1, outerHeight - WALL_THICKNESS);
    }

    /**
     * Retorna el desplazamiento horizontal del hueco interno.
     *
     * @return Desplazamiento horizontal del rectangulo interior.
     */
    private int innerOffsetX(){
        int remaining = width - innerWidth(width);
        return Math.max(0, remaining / 2);
    }

    /**
     * Retorna el color asociado al identificador de una copa.
     *
     * @param n Identificador de la copa.
     * @return Nombre del color definido para el identificador.
     */
    public static String colorForId(int n){
        int index = Math.abs(n - 1) % CUP_COLORS.length;
        return CUP_COLORS[index];
    }
    
    /**
     * Asocia una tapa a esta copa.
     *
     * @param lid Tapa a asociar; puede ser null para limpiar asociacion.
     */
    public void setLid(Lid lid) {
        this.lid = lid;
    }

    /**
     * Retorna la tapa asociada a la copa.
     *
     * @return Instancia de la tapa asociada o null si no existe.
     */
    public Lid getLid() {
        return lid;
    }

    /**
     * Remueve la tapa asociada a esta copa.
     */
    public void removeLid() {
        this.lid = null;
    }

    /**
     * Indica si la copa tiene una tapa asociada.
     *
     * @return true si existe tapa asociada; false en caso contrario.
     */
    public boolean hasLid() {
        return lid != null;
    }

    /**
     * Retorna el tipo concreto del elemento.
     *
     * @return Cadena literal cup.
     */
    @Override
    public String getType() {
        return "cup";
    }

    /**
     * Retorna la familia logica principal del elemento.
     */
    @Override
    public ElementKind getKind(){
        return ElementKind.CUP;
    }

    /**
     * Retorna true cuando este elemento puede contener el ancho indicado.
     */
    @Override
    public boolean canContainLogicalWidth(int incomingLogicalWidth){
        return incomingLogicalWidth < logicalWidth();
    }

    /**
     * Retorna la vista como taza de este elemento.
     */
    @Override
    public Cup asCup(){
        return this;
    }

    /**
     * Indica si esta taza tiene tapa enlazada.
     */
    @Override
    public boolean hasLinkedLid(){
        return hasLid();
    }

    /**
     * Retorna la tapa enlazada a esta taza.
     */
    @Override
    public Lid getLinkedLid(){
        return getLid();
    }

    /**
     * Enlaza la tapa companera de esta taza.
     */
    @Override
    public void linkLid(Lid lid){
        setLid(lid);
    }
    
    /**
     * Dibuja la copa en la posicion indicada.
        *
        * @param x Coordenada horizontal en pixeles.
        * @param y Coordenada vertical en pixeles.
     */
    @Override
    public void draw(int x, int y) {
        this.xPosition = x; 
        this.yPosition = y;
            
        cupShape.changePosition(x, y);
        cupShape.makeVisible();

        cupInnerShape.changePosition(x + innerOffsetX(), y);
        cupInnerShape.makeVisible();
        
    }
    
    /**
     * Borra la copa del canvas.
     */
    @Override
    public void erase(){
        cupShape.makeInvisible();
        cupInnerShape.makeInvisible();
    }
    
}


