package StackingCups;


/**
 * Write a description of class Lid here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

public class Lid extends StackableElement {

    public Lid(int number) {
        super(number);
    }

    @Override
    public String getType() {
        return "lid";
    }
}



