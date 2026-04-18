package StackingCups;

/**
 * Taza especial del ciclo 4 con comportamiento abridor.
 *
 * Al entrar elimina tapas que bloquean su trayectoria de apilamiento.
 *
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */
public class OpenerCup extends Cup {

    /**
     * Construye una taza abridora con el id dado.
     *
     * @param n Identificador positivo del elemento.
     */
    public OpenerCup(int n){
        super(n);
    }

    // --- CICLO 4 ---
    /**
     * Retorna el tipo logico de esta taza especial.
     */
    @Override
    public String getType(){
        return "opener";
    }

    /**
     * Activa la eliminacion de tapas bloqueantes al insertar.
     */
    @Override
    public boolean removesBlockingLids(){
        return true;
    }
}
