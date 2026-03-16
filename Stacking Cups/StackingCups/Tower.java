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
        private int width;
        private int maxHeight;
        private int height;
        
        /**
         * Constructor de la clase torre
         */
        public Tower(int width, int maxHeight) {
            elements = new ArrayList<StackableElement>();
            lastOperationOk = true;
            this. width = width;
            this.maxHeight = maxHeight;
        }
    
        
        /**
         * ===================================================================
         * ManageCup
         * ===================================================================
         */
        public void pushCup(int n) {
    
            if (validateElement(n)){
                Cup cup= new Cup(n);
                elements.add(cup);
                lastOperationOk = true;
            }
        }
        
        public void popCup(){
            validateList("Cup");
        }
        
        public void removeCup(int n){
            removeElement("Cup", n);
        }
        
        /**
         * ===================================================================
         * ManageLid
         * ===================================================================
         */
        
        public void pushLid(int n) {
    
            if (validateElement(n)){
                Lid lid= new Lid(n);
                elements.add(lid);
                lastOperationOk = true;
            }
        }
        
        public void popLid(){
            validateList("Lid");
        }
        
        public void removeLid(int n){
            removeElement("Lid", n);
        }
        
        /**
         * ===================================================================
         * Metodos adicionales para los Manage
         * ===================================================================
         */
        private boolean validateElement(int n){
            
            if (2*n-1 > this.width || 2*n-1 > this.height){
                lastOperationOk = false;
                return false;
            }
            else{
                for (StackableElement e: elements){
                    if (e.getId() == n){
                        /* mensaje de elemento ya existente */
                        lastOperationOk = false;
                        return false;
                    }
                }
                lastOperationOk = true;
                return true;
            }
        }
        
        private boolean validateList(String type){
            if (!elements.isEmpty()){
                deleteLastElement(type);
                lastOperationOk = true;
                return true;
            }
            else{
                /* mensaje de error */
                lastOperationOk = false;
                return false;
            }
        }
        
        private void deleteLastElement(String type){
            for (int e = elements.size() - 1; e >= 0; e--){
                StackableElement actualElement = elements.get(e);
                if (actualElement.getType().equals(type)){
                    elements.remove(actualElement);
                    lastOperationOk = true;
                    break;
                }
            }
        }
        
        private void removeElement(String type, int n){
            for (int e = 0; e < elements.size(); e++){
                StackableElement actualElement = elements.get(e);
                if (actualElement.getType().equals(type) && actualElement.getId() == n){
                    elements.remove(actualElement);
                    lastOperationOk = true;
                    break;
                }
            }
        }
    
        /**
         * ===================================================================
         * Reorganize tower
         * ===================================================================
         */
        
        public void orderTower(){
            for(int e = 0; e < elements.size() - 1; e++){
                for(int n = e + 1; n < elements.size(); n++){
                    if ((elements.get(e).getWidth() > elements.get(n).getWidth()) || (elements.get(e).getWidth() == elements.get(n).getWidth() && elements.get(e).getHeight() > elements.get(n).getHeight())){
                        StackableElement temp = elements.get(n);
                        elements.set(n, elements.get(e));
                        elements.set(e, temp);
                    }
                }
            }
            lastOperationOk = true;
        }
        
        public void reverseTower(){
            for(int e = 0; e < elements.size()/2; e++){
                StackableElement temp = elements.get((elements.size() - 1) - e);
                elements.set((elements.size() - 1) - e, elements.get(e));
                elements.set(e, temp);
            }
            lastOperationOk = true;
        }
        
    /**
     * ===================================================================
     * Consult Information
     * ===================================================================
     */
    public int height() {
        return elements.size();
    }

    public String[][] stackingItems() {

        String[][] result = new String[elements.size()][2];

        for (int i = 0; i < elements.size(); i++) {
            result[i][0] = elements.get(i).getType();
            result[i][1] = String.valueOf(elements.get(i).getId());
        }

        return result;
    }

    public List<StackableElement> getElements() {
        return elements;
    }
    
    /**
     * ===================================================================
     * Set Visibility
     * ===================================================================
     */
    
    /**
     * ===================================================================
     * Exit simulator
     * ===================================================================
     */
    
    /**
     * ===================================================================
     * Painting
     * ===================================================================
     */
    
}
