package StackingCups;
import shapes.*;

 
/**
 * Write a description of class Cup here.
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
    private static final int WALL_THICKNESS = 10;

    private Lid lid;
    private final Rectangle cupShape;
    private final Rectangle cupInnerShape;

    /**
     * Construye una copa a partir de su id y define su tamano visual.
     */
    public Cup(int n){
        
        id = n;
        width = (2*n - 1)*10;
        height = (2*n - 1)*10;
        
        cupShape = new Rectangle();
        cupShape.changeSize(height, width);
        cupShape.changeColor(colorForId(n));

        cupInnerShape = new Rectangle();
        cupInnerShape.changeSize(innerHeight(height), innerWidth(width));
        cupInnerShape.changeColor("white");
        
    }

    /**
     * Retorna el ancho interior visible de la copa.
     */
    private int innerWidth(int outerWidth){
        return Math.max(1, outerWidth - 2 * WALL_THICKNESS);
    }

    /**
     * Retorna la altura interior visible de la copa.
     */
    private int innerHeight(int outerHeight){
        return Math.max(1, outerHeight - WALL_THICKNESS);
    }

    /**
     * Retorna el desplazamiento horizontal del hueco interno.
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
     */
    public boolean hasLid() {
        return lid != null;
    }

    /**
     * Retorna el tipo concreto del elemento.
     */
    @Override
    public String getType() {
        return "cup";
    }
    
    /**
     * Dibuja la copa en la posicion indicada.
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


