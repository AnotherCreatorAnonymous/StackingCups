package StackingCups;

import java.util.*;

/**
 * Write a description of class Cup here.
 * 
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */

public class Cup extends StackableElement {

    private Lid lid;

    public Cup(int n){
        id = n;
        width = 2*n - 1;
        height = 2*n - 1;
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
}


