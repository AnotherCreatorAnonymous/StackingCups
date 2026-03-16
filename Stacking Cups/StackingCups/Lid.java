package StackingCups;
import javax.swing.*;
import java.util.*;
import shapes.*;


/**
 * Write a description of class Lid here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

public class Lid extends StackableElement {
    
    protected Rectangle lid;
    
    public Lid(int n){
        id = n;
        width = 2*n - 1;
        height = 1;
        lid = new Rectangle();
        lid.changeSize(height, width);
    }


    @Override
    public String getType() {
        return "lid";
    }
    
    @Override
    public void draw(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        
        if (lid != null) {
            lid.changePosition(x, y);
            
            lid.changeColor("black"); 
            
            lid.makeVisible();
        }
    }
    

}



