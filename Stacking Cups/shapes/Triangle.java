package shapes;
import java.awt.*;

/**
 * A triangle that can be manipulated and that draws itself on a canvas.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 1.0  (15 July 2000)
 */

public class Triangle extends ShapeBase {
    
    public static final int VERTICES = 3;
    
    private int height;
    private int width;

    /**
     * Create a new triangle at default position with default color.
     */
    public Triangle(){
        super(140, 15, "green");
        height = 30;
        width = 40;
    }

    /**
     * Change the size to the new size
     * @param newHeight the new height in pixels. newHeight must be >=0.
     * @param newWidht the new width in pixels. newWidht must be >=0.
     */
    public void changeSize(int newHeight, int newWidth) {
        eraseShape();
        height = newHeight;
        width = newWidth;
        redrawShape();
    }

    /**
     * Construye la forma concreta de este triangulo.
     */
    @Override
    protected Shape buildShape(){
        int[] xpoints = { xPosition, xPosition + (width / 2), xPosition - (width / 2) };
        int[] ypoints = { yPosition, yPosition + height, yPosition + height };
        return new Polygon(xpoints, ypoints, 3);
    }
}
