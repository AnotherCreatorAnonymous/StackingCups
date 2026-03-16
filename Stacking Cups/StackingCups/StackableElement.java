package StackingCups;
import javax.swing.*;
import java.util.*;
import shapes.*;


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
    protected int xPosition;
    protected int yPosition;
    private boolean isVisible;
    protected Rectangle StackableElement;
    
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
    
    public abstract void draw(int x, int y);
    
    public void makeVisible(){
        isVisible = true;
        draw(xPosition, yPosition); 
    }

    public void makeInvisible(){
        isVisible = false;
        erase();
    }
    
    public void erase() {
        if (StackableElement != null) {
            StackableElement.makeInvisible();
            this.isVisible = false;
        }
    }
}

