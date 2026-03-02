package StackingCups;

/**
 * Write a description of class StackableElement here.
 * 
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */

public abstract class StackableElement {

    protected int number;

    public StackableElement(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public abstract String getType(); // "cup" o "lid"
}

