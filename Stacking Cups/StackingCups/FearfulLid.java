package StackingCups;

/**
 * Tapa especial del ciclo 4 con comportamiento temeroso.
 *
 * Solo puede entrar a la torre si su taza companera ya existe.
 *
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */
public class FearfulLid extends Lid {

    /**
     * Construye una tapa fearful con el id dado.
     *
     * @param n Identificador positivo del elemento.
     */
    public FearfulLid(int n){
        super(n);
    }

    // --- CICLO 4 ---
    /**
     * Retorna el tipo logico de esta tapa especial.
     */
    @Override
    public String getType(){
        return "fearful";
    }

    /**
     * Requiere la presencia previa de taza companera.
     */
    @Override
    public boolean requiresCompanionCup(){
        return true;
    }
}
