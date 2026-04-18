package shapes;
import java.awt.Shape;

/**
 * Clase base comun para figuras dibujables en el canvas.
 *
 * Centraliza visibilidad, color, posicion y movimientos para evitar
 * duplicacion entre Rectangle, Circle y Triangle.
 *
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */
public abstract class ShapeBase {

    protected int xPosition;
    protected int yPosition;
    protected String color;
    protected boolean isVisible;

    /**
     * Construye una figura con posicion y color inicial.
     *
     * @param initialX Coordenada x inicial.
     * @param initialY Coordenada y inicial.
     * @param initialColor Color inicial de dibujo.
     */
    protected ShapeBase(int initialX, int initialY, String initialColor){
        xPosition = initialX;
        yPosition = initialY;
        color = initialColor;
        isVisible = false;
    }

    /**
     * Hace visible la figura en el canvas.
     */
    public void makeVisible(){
        isVisible = true;
        redrawShape();
    }

    /**
     * Oculta la figura del canvas.
     */
    public void makeInvisible(){
        eraseShape();
        isVisible = false;
    }

    /**
     * Mueve la figura unos pixeles a la derecha.
     */
    public void moveRight(){
        moveHorizontal(20);
    }

    /**
     * Mueve la figura unos pixeles a la izquierda.
     */
    public void moveLeft(){
        moveHorizontal(-20);
    }

    /**
     * Mueve la figura unos pixeles arriba.
     */
    public void moveUp(){
        moveVertical(-20);
    }

    /**
     * Mueve la figura unos pixeles abajo.
     */
    public void moveDown(){
        moveVertical(20);
    }

    /**
     * Mueve la figura horizontalmente.
     *
     * @param distance Distancia en pixeles.
     */
    public void moveHorizontal(int distance){
        eraseShape();
        xPosition += distance;
        redrawShape();
    }

    /**
     * Mueve la figura verticalmente.
     *
     * @param distance Distancia en pixeles.
     */
    public void moveVertical(int distance){
        eraseShape();
        yPosition += distance;
        redrawShape();
    }

    /**
     * Mueve la figura horizontalmente de forma gradual.
     *
     * @param distance Distancia total en pixeles.
     */
    public void slowMoveHorizontal(int distance){
        int delta;

        if(distance < 0){
            delta = -1;
            distance = -distance;
        }
        else{
            delta = 1;
        }

        for(int i = 0; i < distance; i++){
            xPosition += delta;
            redrawShape();
        }
    }

    /**
     * Mueve la figura verticalmente de forma gradual.
     *
     * @param distance Distancia total en pixeles.
     */
    public void slowMoveVertical(int distance){
        int delta;

        if(distance < 0){
            delta = -1;
            distance = -distance;
        }
        else{
            delta = 1;
        }

        for(int i = 0; i < distance; i++){
            yPosition += delta;
            redrawShape();
        }
    }

    /**
     * Cambia el color de la figura.
     *
     * @param newColor Nuevo color.
     */
    public void changeColor(String newColor){
        color = newColor;
        redrawShape();
    }

    /**
     * Cambia la posicion absoluta de la figura.
     *
     * @param newX Nueva coordenada x.
     * @param newY Nueva coordenada y.
     */
    public void changePosition(int newX, int newY){
        eraseShape();
        xPosition = newX;
        yPosition = newY;
        redrawShape();
    }

    /**
     * Borra la figura actual cuando esta visible.
     */
    protected void eraseShape(){
        if(isVisible){
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }

    /**
     * Redibuja la figura actual cuando esta visible.
     */
    protected void redrawShape(){
        if(isVisible){
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color, buildShape());
            canvas.wait(10);
        }
    }

    /**
     * Construye la forma geometrica concreta para dibujo.
     *
     * @return Forma lista para ser renderizada.
     */
    protected abstract Shape buildShape();
}
