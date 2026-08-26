package co.unicauca.taller2.ui;

import co.unicauca.taller2.model.Role;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación por defecto de {@link RoleMenuProvider}.
 *
 * Cada rol tiene asociada una lista fija de opciones de menú. Esta clase
 * es sustituible por cualquier otra implementación de
 * {@link RoleMenuProvider} (por ejemplo, una que lea las opciones desde
 * la base de datos) sin afectar a quien la consume (LSP).
 */
public class DefaultRoleMenuProvider implements RoleMenuProvider {

    private final Map<Role, List<String>> menusByRole = new EnumMap<>(Role.class);

    public DefaultRoleMenuProvider() {
        menusByRole.put(Role.ADMINISTRADOR, List.of(
                "Gestionar usuarios del sistema",
                "Ver reportes generales",
                "Configurar parámetros del sistema"
        ));
        menusByRole.put(Role.AUTOR_PREGUNTAS, List.of(
                "Crear nueva pregunta",
                "Editar mis preguntas",
                "Ver historial de preguntas"
        ));
        menusByRole.put(Role.REVISOR, List.of(
                "Revisar preguntas pendientes",
                "Aprobar o rechazar preguntas",
                "Ver historial de revisiones"
        ));
        menusByRole.put(Role.DOCENTE, List.of(
                "Ver mis cursos",
                "Generar evaluaciones",
                "Consultar resultados de estudiantes"
        ));
        menusByRole.put(Role.ESTUDIANTE, List.of(
                "Ver mis evaluaciones pendientes",
                "Presentar evaluación",
                "Consultar mis calificaciones"
        ));
    }

    @Override
    public List<String> getMenuOptions(Role role) {
        return menusByRole.getOrDefault(role, List.of());
    }
}
