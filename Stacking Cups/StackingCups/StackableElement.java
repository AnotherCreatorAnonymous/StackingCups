package StackingCups;


/**
 * Representa un elemento apilable abstracto dentro del simulador.
 *
 * Centraliza propiedades comunes de posicion y tamano para tazas y tapas,
 * y define la interfaz minima para dibujar y ocultar cada subclase.
 * 
 * @author Carlos Felipe Jimenez Sposito
 * @version 2.0
 */

public abstract class StackableElement {

    public static final int UNIT_PIXELS = 10;

    protected int id;
    protected int width;
    protected int height;
    protected String type;
    protected int xPosition;
    protected int yPosition;

    // --- CICLO 4 ---
    /**
     * Clasifica el elemento en familia cup o lid para reglas generales.
     */
    public enum ElementKind {
        CUP,
        LID
    }
    
    /**
     * Retorna el tipo concreto del elemento apilable.
        *
        * @return Tipo logico del elemento, por ejemplo cup o lid.
     */
    public abstract String getType();

    /**
     * Retorna la familia logica principal del elemento.
     */
    public abstract ElementKind getKind();
    
    /**
     * Dibuja el elemento en la posicion indicada.
        *
        * @param x Coordenada horizontal en pixeles.
        * @param y Coordenada vertical en pixeles.
     */
    public abstract void draw(int x, int y);
    
    /**
     * Borra la representacion visual del elemento.
     */
    public abstract void erase();

    // --- CICLO 2 ---
    /**
     * Retorna el ancho logico del elemento en celdas de 1 cm.
     */
    public int logicalWidth(){
        return width / UNIT_PIXELS;
    }

    /**
     * Retorna la altura logica del elemento en celdas de 1 cm.
     */
    public int logicalHeight(){
        return Math.max(1, height / UNIT_PIXELS);
    }

    /**
     * Indica si este elemento puede contener internamente un ancho logico.
     */
    public boolean canContainLogicalWidth(int incomingLogicalWidth){
        return false;
    }

    // --- CICLO 4 ---
    /**
     * Indica si el elemento debe validar existencia de taza companera.
     */
    public boolean requiresCompanionCup(){
        return false;
    }

    /**
     * Indica si el elemento elimina tapas bloqueantes al entrar.
     */
    public boolean removesBlockingLids(){
        return false;
    }

    /**
     * Indica si el elemento desplaza hacia arriba elementos mas pequenos.
     */
    public boolean pushesSmallerElementsUp(){
        return false;
    }

    /**
     * Indica si el elemento debe ubicarse como base de su companero.
     */
    public boolean prefersBasePlacement(){
        return false;
    }

    /**
     * Indica si este elemento puede vincularse como companero por id.
     */
    public boolean isCompanionOf(StackableElement other){
        if(other == null){
            return false;
        }
        return getKind() != other.getKind() && id == other.id;
    }

    /**
     * Retorna la vista como Cup cuando aplica.
     */
    public Cup asCup(){
        return null;
    }

    /**
     * Retorna la vista como Lid cuando aplica.
     */
    public Lid asLid(){
        return null;
    }

    /**
     * Retorna true cuando la taza tiene tapa enlazada.
     */
    public boolean hasLinkedLid(){
        return false;
    }

    /**
     * Retorna la tapa enlazada cuando existe.
     */
    public Lid getLinkedLid(){
        return null;
    }

    /**
     * Permite enlazar una tapa al elemento cuando aplica.
     */
    public void linkLid(Lid lid){
        // Implementacion por defecto para elementos que no enlazan tapas.
    }
    
    /**
     * Retorna el identificador unico del elemento.
     */
    public int getId(){
        return id;
    }
    
    /**
     * Retorna el ancho del elemento en pixeles.
     */
    public int getWidth(){
        return width;
    }
    
    /**
     * Retorna la altura del elemento en pixeles.
     */
    public int getHeight(){
        return height;
    }
    
    /**
     * Activa la visibilidad del elemento y lo dibuja en su posicion actual.
     */
    public void makeVisible(){
        draw(xPosition, yPosition); 
    }

    /**
     * Desactiva la visibilidad del elemento y lo borra del canvas.
     */
    public void makeInvisible(){
        erase();
    }
    
}

