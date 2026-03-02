package StackingCups;

import java.util.*;

/**
 * Write a description of class Tower here.
 * 
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */

public class Tower {

    private List<StackableElement> elements;
    private boolean lastOperationOk;

    public Tower(int width, int maxHeight) {
        elements = new ArrayList<>();
        lastOperationOk = true;
    }

    public boolean push(StackableElement element) {

        if (existsNumber(element.getNumber())) {
            lastOperationOk = false;
            return false;
        }

        elements.add(element);
        lastOperationOk = true;
        return true;
    }

    public boolean popLastOfType(String type) {

        for (int i = elements.size() - 1; i >= 0; i--) {
            if (elements.get(i).getType().equals(type)) {
                elements.remove(i);
                lastOperationOk = true;
                return true;
            }
        }

        lastOperationOk = false;
        return false;
    }

    public boolean removeByNumber(String type, int n) {

        for (StackableElement e : elements) {
            if (e.getType().equals(type) && e.getNumber() == n) {
                elements.remove(e);
                lastOperationOk = true;
                return true;
            }
        }

        lastOperationOk = false;
        return false;
    }

    public void orderDescending() {
        elements.sort((a, b) -> b.getNumber() - a.getNumber());
        lastOperationOk = true;
    }

    public void reverse() {
        Collections.reverse(elements);
        lastOperationOk = true;
    }

    public int height() {
        return elements.size();
    }

    public String[][] stackingItems() {

        String[][] result = new String[elements.size()][2];

        for (int i = 0; i < elements.size(); i++) {
            result[i][0] = elements.get(i).getType();
            result[i][1] = String.valueOf(elements.get(i).getNumber());
        }

        return result;
    }

    public boolean ok() {
        return lastOperationOk;
    }

    private boolean existsNumber(int n) {

        for (StackableElement e : elements) {
            if (e.getNumber() == n) return true;
        }

        return false;
    }

    public List<StackableElement> getElements() {
        return elements;
    }
}
