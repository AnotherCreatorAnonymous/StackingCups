package StackingCups;

/**
 * Tapa especial del ciclo 4 con comportamiento crazy.
 *
 * En lugar de ubicarse como tapa superior, se posiciona como base
 * de su taza companera cuando esta existe en la torre.
 *
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */
public class CrazyLid extends Lid {

    /**
     * Construye una tapa crazy con el id dado.
     *
     * @param n Identificador positivo del elemento.
     */
    public CrazyLid(int n){
        super(n);
    }

    // --- CICLO 4 ---
    /**
     * Retorna el tipo logico de esta tapa especial.
     */
    @Override
    public String getType(){
        return "crazy";
    }

    /**
     * Requiere la presencia previa de taza companera.
     */
    @Override
    public boolean requiresCompanionCup(){
        return true;
    }

    /**
     * Indica que debe insertarse como base de su taza companera.
     */
    @Override
    public boolean prefersBasePlacement(){
        return true;
    }
}
