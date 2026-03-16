package StackingCups;
import javax.swing.*;
import java.util.*;
import shapes.*;


/**
 * Write a description of class Cup here.
 * 
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */

public class Cup extends StackableElement {

    private Lid lid;
    protected Rectangle cup;

    public Cup(int n){
        this.id = n;
        this.width = (2*n - 1)*10;
        this.height = (2*n - 1)*10;
        this.cup = new Rectangle();
        this.cup.changeSize(this.height, this.width);
        
    }
    
    public void setLid(Lid lid) {
        this.lid = lid;
    }

    public void removeLid() {
        this.lid = null;
    }

    public boolean hasLid() {
        return lid != null;
    }

    @Override
    public String getType() {
        return "cup";
    }
    
    @Override
    public void draw(int x, int y) {
        this.xPosition = x; 
        this.yPosition = y;
        if (cup != null) {
            cup.changePosition(x, y);
            cup.makeVisible();
        }
    }
    
}


