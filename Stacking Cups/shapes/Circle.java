package shapes;
import java.awt.*;
import java.awt.geom.*;

/**
 * A circle that can be manipulated and that draws itself on a canvas.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 1.0.  (15 July 2000) 
 */
public class Circle extends ShapeBase {
    public static final double PI=3.1416;
    
    private int diameter;
    
    public Circle(){
        super(20, 15, "blue");
        diameter = 30;
    }
    
    /**
     * Change the size.
     * @param newDiameter the new size (in pixels). Size must be >=0.
     */
    public void changeSize(int newDiameter){
        eraseShape();
        diameter = newDiameter;
        redrawShape();
    }

    /**
     * Construye la forma concreta de este circulo.
     */
    @Override
    protected Shape buildShape(){
        return new Ellipse2D.Double(xPosition, yPosition, diameter, diameter);
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
        super.changePosition(newX, newY);
    }
}
