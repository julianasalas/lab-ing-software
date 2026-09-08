package co.unicauca.bancopreguntas.infra;

/**
 * Contrato del patron Observer.
 *
 * Capa: transversal (infra).
 *
 * Se mantiene deliberadamente con un unico metodo para cumplir el principio de
 * segregacion de interfaces (ISP): quien quiera enterarse de un cambio de estado
 * del sujeto solo esta obligado a implementar update(), nada mas.
 */
public interface Observer {

    /**
     * Invocado por el {@link Subject} cada vez que su estado cambia.
     * El observador consulta ("pull") la informacion que necesite del sujeto.
     */
    void update();
}
