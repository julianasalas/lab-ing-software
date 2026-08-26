package co.unicauca.taller2.ui;

import co.unicauca.taller2.model.Role;

import java.util.List;

/**
 * Abstracción encargada de decidir qué opciones de menú/tablero se
 * muestran a un usuario según su rol.
 *
 * <p>Separar esta decisión en una interfaz permite agregar nuevos roles u
 * opciones de menú creando una nueva implementación (o extendiendo la
 * existente) sin modificar el código de la ventana principal
 * {@link MainMenuFrame} (Principio Abierto/Cerrado - OCP).</p>
 */
public interface RoleMenuProvider {

    /**
     * @param role rol del usuario autenticado
     * @return lista de etiquetas de las opciones de menú disponibles para ese rol
     */
    List<String> getMenuOptions(Role role);
}
