package StackingCups;
import shapes.*;


/**
 * Modela una tapa apilable que puede asociarse a una taza por id.
 *
 * La tapa conserva la base impar del identificador y una altura fija
 * de una unidad logica para cumplir las reglas del problema.
 * 
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */

public class Lid extends StackableElement {
    private static final int LID_HEIGHT_UNITS = 1;
    
    protected Rectangle lidShape;
    
    /**
     * Construye una tapa a partir de su id y define su tamano visual.
     *
     * @param n Identificador positivo de la tapa.
     */
    public Lid(int n){
        id = n;
        width = (2*n - 1) * StackableElement.UNIT_PIXELS;
        height = LID_HEIGHT_UNITS * StackableElement.UNIT_PIXELS;
        
        lidShape = new Rectangle();
        lidShape.changeSize(height, width);
        lidShape.changeColor(Cup.colorForId(n));
    }

    /**
     * Retorna el tipo concreto del elemento.
     *
     * @return Cadena literal lid.
     */
    @Override
    public String getType() {
        return "lid";
    }

    /**
     * Retorna la familia logica principal del elemento.
     */
    @Override
    public ElementKind getKind(){
        return ElementKind.LID;
    }

    /**
     * Retorna la vista como tapa de este elemento.
     */
    @Override
    public Lid asLid(){
        return this;
    }
    
    /**
     * Dibuja la tapa en la posicion indicada.
        *
        * @param x Coordenada horizontal en pixeles.
        * @param y Coordenada vertical en pixeles.
     */
    @Override
    public void draw(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        
        lidShape.changePosition(x, y);         
        lidShape.makeVisible();
    }
    
    /**
     * Borra la tapa del canvas.
     */
    @Override
    public void erase(){
        lidShape.makeInvisible();
    }
}



