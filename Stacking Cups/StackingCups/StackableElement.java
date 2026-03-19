package StackingCups;


/**
 * Write a description of class StackableElement here.
 * 
 * @author Carlos Felipe Jimenez Sposito
 * @version 2.0
 */

public abstract class StackableElement {

    protected int id;
    protected int width;
    protected int height;
    protected String type;
    protected int xPosition;
    protected int yPosition;
    
    /**
     * Retorna el tipo concreto del elemento apilable.
     */
    public abstract String getType();
    
    /**
     * Dibuja el elemento en la posicion indicada.
     */
    public abstract void draw(int x, int y);
    
    /**
     * Borra la representacion visual del elemento.
     */
    public abstract void erase();
    
    /**
     * Retorna el identificador unico del elemento.
     */
    public int getId(){
        return id;
    }
    
    /**
     * Retorna el ancho del elemento en pixeles.
     */
    public int getWidth(){
        return width;
    }
    
    /**
     * Retorna la altura del elemento en pixeles.
     */
    public int getHeight(){
        return height;
    }
    
    /**
     * Activa la visibilidad del elemento y lo dibuja en su posicion actual.
     */
    public void makeVisible(){
        draw(xPosition, yPosition); 
    }

    /**
     * Desactiva la visibilidad del elemento y lo borra del canvas.
     */
    public void makeInvisible(){
        erase();
    }
    
}

