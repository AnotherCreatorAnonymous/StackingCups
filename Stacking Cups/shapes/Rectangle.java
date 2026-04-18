package shapes;
import java.awt.*;

/**
 * A rectangle that can be manipulated and that draws itself on a canvas.
 * 
 * @author  Michael Kolling and David J. Barnes (Modified)
 * @version 1.0  (15 July 2000)()
 */

public class Rectangle extends ShapeBase {

    public static final int EDGES = 4;
    
    private int height;
    private int width;
    private boolean zoomed;

    /**
     * Create a new rectangle at default position with default color.
     */
    
    public Rectangle(){
        super(70, 15, "magenta");
        height = 30;
        width = 40;
        zoomed = false;
    }
    
    /**
     * Change the size to the new size
     * @param newHeight the new height in pixels. newHeight must be >=0.
     * @param newWidht the new width in pixels. newWidth must be >=0.
     */
    public void changeSize(int newHeight, int newWidth) {
        eraseShape();
        height = newHeight;
        width = newWidth;
        redrawShape();
    }

    /**
     * Construye la forma concreta de este rectangulo.
     */
    @Override
    protected Shape buildShape(){
        return new java.awt.Rectangle(xPosition, yPosition, width, height);
    }
    
    public int perimeter(){
        return (2 * height) + (2 * width);
    }
    
    public void zoom(){
        if (!zoomed){
            changeSize(height*2, width*2);
            zoomed = true;
        }
        else {
            changeSize(height/2, width/2);
            zoomed = false;
        }
    }
    
    public void walk(int times){
        int count = 0;
        while (count < times){
            if (times < 0){
                slowMoveVertical(1);
                slowMoveHorizontal(-1);
            }
            if (times > 0){
                slowMoveVertical(1);
                slowMoveHorizontal(1);
            } 
            count = 1 + count;
        }
    }
}

