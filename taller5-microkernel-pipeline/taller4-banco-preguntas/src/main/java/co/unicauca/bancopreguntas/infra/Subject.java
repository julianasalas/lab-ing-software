package co.unicauca.bancopreguntas.infra;

import java.util.ArrayList;
import java.util.List;

/**
 * Sujeto observable del patron Observer.
 *
 * Capa: transversal (infra).
 *
 * Cualquier clase que deba avisar de sus cambios de estado hereda de esta clase.
 * El sujeto solo conoce la abstraccion {@link Observer}, nunca a las vistas
 * concretas: por eso se pueden agregar vistas nuevas sin modificar esta clase
 * ni el servicio de dominio (principio abierto/cerrado, OCP).
 */
public abstract class Subject {

    private final List<Observer> observers = new ArrayList<>();

    /** Registra un observador. Ignora nulos y duplicados. */
    public void attach(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /** Da de baja a un observador. */
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    /** Numero de observadores registrados (util para pruebas unitarias). */
    public int countObservers() {
        return observers.size();
    }

    /**
     * Notifica a todos los observadores registrados.
     * Se itera sobre una copia para que un observador pueda darse de baja
     * dentro de su propio update() sin romper la iteracion.
     */
    protected void notifyObservers() {
        for (Observer observer : new ArrayList<>(observers)) {
            observer.update();
        }
    }
}
