package StackingCups;

/**
 * Write a description of class StackableElement here.
 * 
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */

public abstract class StackableElement {

    protected int id;
    protected int width;
    protected int height;
    protected String type;    
    
    public int getId(){
        return id;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }

    public abstract String getType();
    
    public abstract void draw();
    
    public abstract void makeVisible();
    
    public abstract void makeInvisible();
    
}

