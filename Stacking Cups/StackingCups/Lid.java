package StackingCups;
import shapes.*;


/**
 * Write a description of class Lid here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

public class Lid extends StackableElement {
    
    protected Rectangle lidShape;
    
    /**
     * Construye una tapa a partir de su id y define su tamano visual.
     */
    public Lid(int n){
        id = n;
        width = (2*n - 1)*10;
        height = 10;
        
        lidShape = new Rectangle();
        lidShape.changeSize(height, width);
    }

    /**
     * Retorna el tipo concreto del elemento.
     */
    @Override
    public String getType() {
        return "lid";
    }
    
    /**
     * Dibuja la tapa en la posicion indicada.
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



