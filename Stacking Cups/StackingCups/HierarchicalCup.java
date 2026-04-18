package StackingCups;

/**
 * Taza especial del ciclo 4 con comportamiento jerarquico.
 *
 * Al entrar en la torre debe desplazar hacia arriba los elementos
 * de menor tamano respecto a su ancho logico.
 *
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */
public class HierarchicalCup extends Cup {

    /**
     * Construye una taza jerarquica con el id dado.
     *
     * @param n Identificador positivo del elemento.
     */
    public HierarchicalCup(int n){
        super(n);
    }

    // --- CICLO 4 ---
    /**
     * Retorna el tipo logico de esta taza especial.
     */
    @Override
    public String getType(){
        return "hierarchical";
    }

    /**
     * Activa la regla de desplazamiento de menores al insertar.
     */
    @Override
    public boolean pushesSmallerElementsUp(){
        return true;
    }
}
