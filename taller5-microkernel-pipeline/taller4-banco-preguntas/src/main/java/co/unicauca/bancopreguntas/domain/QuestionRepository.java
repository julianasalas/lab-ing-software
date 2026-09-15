package co.unicauca.bancopreguntas.domain;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistencia de preguntas.
 *
 * Capa: dominio.
 *
 * La interfaz vive en el dominio y su implementacion en la capa de acceso a
 * datos: asi el dominio no depende de la infraestructura, sino la infraestructura
 * del dominio (principio de inversion de dependencias, DIP). Cambiar de un mapa
 * en memoria a una base de datos relacional no obliga a tocar QuestionService.
 */
public interface QuestionRepository {

    /** Todas las preguntas del banco. */
    List<Question> findAll();

    /** Pregunta con el id dado, vacio si no existe. */
    Optional<Question> findById(String id);

    /** Crea o actualiza una pregunta y la retorna. */
    Question save(Question question);
}
