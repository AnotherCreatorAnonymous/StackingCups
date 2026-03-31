package StackingCups;

/**
 * Resuelve y simula escenarios del problema de maraton para Stacking Cups.
 *
 * Esta clase concentra la logica de concurso para mantener separada la
 * simulacion visual implementada en Tower.
 *
 * @author Carlos Felipe Jimenez Sposito
 * @version 1.0
 */
public class TowerContest {

    /**
     * Resuelve si es posible apilar n tazas dentro de una altura maxima h.
     *
     * La decision se basa en la altura minima requerida por una torre ordenada
     * descendente con tapas opcionales no obligatorias.
     *
     * @param n Cantidad de tazas a apilar.
     * @param h Altura maxima disponible en niveles de 1 cm.
     * @return "possible" si existe solucion; "impossible" en caso contrario.
     */
    public String solve(int n, int h){
        if(n <= 0 || h <= 0){
            return "impossible";
        }

        int minHeight = minimumHeightForCups(n);
        if(minHeight <= h){
            return "possible";
        }
        return "impossible";
    }

    /**
     * Simula visualmente una solucion cuando existe; de lo contrario notifica.
     *
     * @param n Cantidad de tazas a simular.
     * @param h Altura maxima permitida para la simulacion.
     */
    public void simulate(int n, int h){
        String result = solve(n, h);
        if(!"possible".equals(result)){
            System.out.println("No existe solucion para n=" + n + " y h=" + h);
            return;
        }

        Tower tower = new Tower(Math.max(1, (2 * n) - 1), Math.max(1, h));
        tower.tower(n);
        for(int i = 1; i <= n; i++){
            tower.pushCup(i);
        }
        tower.orderTower();
        tower.makeVisible();
    }

    /**
     * Calcula la altura minima en niveles para n tazas en este modelo.
     *
     * @param n Cantidad de tazas.
     * @return Altura minima requerida para apilar las tazas.
     */
    private int minimumHeightForCups(int n){
        return n;
    }
}
