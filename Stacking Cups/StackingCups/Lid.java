package StackingCups;


/**
 * Write a description of class Lid here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

public class Lid extends StackableElement {
    
    public Lid(int n){
        id = n;
        width = 2*n - 1;
        height = 1;
    }


    @Override
    public String getType() {
        return "lid";
    }
}



