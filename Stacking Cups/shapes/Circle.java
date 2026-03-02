package shapes;
import java.awt.*;
import java.awt.geom.*;

/**
 * A circle that can be manipulated and that draws itself on a canvas.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 1.0.  (15 July 2000) 
 */
public class Circle{
    public static final double PI=3.1416;
    
    private int diameter;
    private int xPosition;
    private int yPosition;
    private String color;
    private boolean isVisible;
    
    public Circle(){
        diameter = 30;
        xPosition = 20;
        yPosition = 15;
        color = "blue";
        isVisible = false;
    }
       
    public void makeVisible(){
        isVisible = true;
        draw();
    }
    
    public void makeInvisible(){
        erase();
        isVisible = false;
    }
    
    private void draw(){
        if(isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color, 
                new Ellipse2D.Double(xPosition, yPosition, 
                diameter, diameter));
            canvas.wait(10);
        }
    }
    
    private void erase(){
        if(isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }
    
    /**
     * Move the circle a few pixels to the right.
     */
    public void moveRight(){
        moveHorizontal(20);
    }
    
    /**
     * Move the circle a few pixels to the left.
     */
    public void moveLeft(){
        moveHorizontal(-20);
    }
    
    /**
     * Move the circle a few pixels up.
     */
    public void moveUp(){
        moveVertical(-20);
    }
    
    /**
     * Move the circle a few pixels down.
     */
    public void moveDown(){
        moveVertical(20);
    }
    
    /**
     * Move the circle horizontally.
     * @param distance the desired distance in pixels
     */
    public void moveHorizontal(int distance){
        erase();
        xPosition += distance;
        draw();
    }
    
    /**
     * Move the circle vertically.
     * @param distance the desired distance in pixels
     */
    public void moveVertical(int distance){
        erase();
        yPosition += distance;
        draw();
    }
    
    /**
     * Slowly move the circle horizontally.
     * @param distance the desired distance in pixels
     */
    public void slowMoveHorizontal(int distance){
        int delta;
        if(distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }
        for(int i = 0; i < distance; i++){
            xPosition += delta;
            draw();
        }
    }
    
    /**
     * Slowly move the circle vertically
     * @param distance the desired distance in pixels
     */
    public void slowMoveVertical(int distance){
        int delta;
        if(distance < 0) {
            delta = -1;
            distance = -distance;
        }else {
            delta = 1;
        }
        for(int i = 0; i < distance; i++){
            yPosition += delta;
            draw();
        }
    }
    
    /**
     * Change the size.
     * @param newDiameter the new size (in pixels). Size must be >=0.
     */
    public void changeSize(int newDiameter){
        erase();
        diameter = newDiameter;
        draw();
    }
    
    /**
     * Change the color. 
     * @param color the new color. Valid colors are "red", "yellow", "blue", "green",
     * "magenta" and "black".
     */
    public void changeColor(String newColor){
        color = newColor;
        draw();
    }
    
    /**
     * Cambia la posición del círculo a coordenadas absolutas.
     * Este método coloca el círculo en una posición EXACTA en píxeles,
     * no mueve relativamente como moveHorizontal/moveVertical.
     * 
     * DIFERENCIA CLAVE:
     * - moveHorizontal(50) → mueve 50 píxeles desde donde está (RELATIVO)
     * - changePosition(100, 200) → coloca el círculo en (100, 200) (ABSOLUTO)
     * 
     * USO EN HUNGRYSNAKE:
     * Cuando una fruta necesita aparecer en una celda específica del tablero,
     * usamos changePosition() para colocarla directamente en esas coordenadas
     * sin importar dónde estaba antes.
     * 
     * EJEMPLO:
     * Circle c = new Circle();
     * c.makeVisible();
     * c.changePosition(100, 150); // Ahora está en (100, 150)
     * c.changePosition(200, 50);  // Ahora está en (200, 50)
     * // No importa dónde estaba antes, va directamente a la nueva posición
     * 
     * @param newX la nueva posición X (horizontal) en píxeles
     * @param newY la nueva posición Y (vertical) en píxeles
     */
    public void changePosition(int newX, int newY){
        erase();           // Borra el círculo de su posición actual
        xPosition = newX;  // Actualiza la posición X
        yPosition = newY;  // Actualiza la posición Y
        draw();            // Dibuja el círculo en la nueva posición
    }
}
