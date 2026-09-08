package co.unicauca.bancopreguntas.access;

import co.unicauca.bancopreguntas.domain.Question;
import co.unicauca.bancopreguntas.domain.QuestionDistractors;
import co.unicauca.bancopreguntas.domain.QuestionRepository;
import co.unicauca.bancopreguntas.domain.QuestionState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementacion del repositorio con un mapa en memoria.
 *
 * Capa: acceso a datos.
 *
 * Como permite el enunciado del taller, la persistencia se resuelve con una
 * estructura simple (LinkedHashMap, que conserva el orden de insercion) en lugar
 * de una base de datos relacional. Al implementar la interfaz del dominio, puede
 * sustituirse por una version JDBC/JPA sin tocar ninguna otra clase.
 */
public class QuestionImplRepository implements QuestionRepository {

    private final Map<String, Question> questions = new LinkedHashMap<>();

    /** Crea el repositorio con un conjunto de preguntas de ejemplo. */
    public QuestionImplRepository() {
        this(true);
    }

    /**
     * @param loadSeedData false para arrancar vacio (util en pruebas unitarias).
     */
    public QuestionImplRepository(boolean loadSeedData) {
        if (loadSeedData) {
            loadSeedData();
        }
    }

    @Override
    public List<Question> findAll() {
        return new ArrayList<>(questions.values());
    }

    @Override
    public Optional<Question> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(questions.get(id));
    }

    @Override
    public Question save(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("La pregunta a guardar no puede ser nula");
        }
        questions.put(question.getId(), question);
        return question;
    }

    /** Datos de ejemplo para que la interfaz tenga contenido al arrancar. */
    private void loadSeedData() {
        save(new Question(
                "P-001",
                "Pregunta sobre DDD",
                "Cual es el objetivo principal de DDD?",
                new QuestionDistractors(
                        "Disenar bases de datos",
                        "Modelar el dominio del negocio",
                        "Eliminar UML",
                        "Crear interfaces graficas"),
                1,
                QuestionState.BORRADOR));

        save(new Question(
                "P-002",
                "Pregunta sobre SOLID",
                "Que promueve el principio de inversion de dependencias?",
                new QuestionDistractors(
                        "Depender de abstracciones y no de implementaciones",
                        "Usar siempre herencia multiple",
                        "Escribir clases con muchas responsabilidades",
                        "Evitar el uso de interfaces"),
                0,
                QuestionState.BORRADOR));

        save(new Question(
                "P-003",
                "Pregunta sobre patrones",
                "Que problema resuelve el patron Observer?",
                new QuestionDistractors(
                        "Crear objetos sin exponer su constructor",
                        "Notificar cambios de estado a multiples dependientes",
                        "Recorrer colecciones sin exponer su estructura",
                        "Adaptar interfaces incompatibles"),
                1,
                QuestionState.PENDIENTE_REVISION));

        save(new Question(
                "P-004",
                "Pregunta sobre arquitectura",
                "En una arquitectura en capas, hacia donde fluyen las dependencias?",
                new QuestionDistractors(
                        "De las capas inferiores hacia las superiores",
                        "En ambos sentidos indistintamente",
                        "De las capas superiores hacia las inferiores",
                        "Solo entre capas no adyacentes"),
                2,
                QuestionState.PENDIENTE_REVISION));

        save(new Question(
                "P-005",
                "Pregunta sobre MVC",
                "Cual es la responsabilidad del controlador en el patron MVC?",
                new QuestionDistractors(
                        "Almacenar los datos de la aplicacion",
                        "Renderizar la interfaz grafica",
                        "Coordinar las peticiones de la vista con el modelo",
                        "Definir el esquema de la base de datos"),
                2,
                QuestionState.ELIMINADA));

        save(new Question(
                "P-006",
                "Pregunta sobre pruebas",
                "Que caracteriza a una prueba unitaria?",
                new QuestionDistractors(
                        "Verifica el sistema completo desplegado",
                        "Verifica una unidad de codigo de forma aislada",
                        "Requiere siempre conexion a base de datos",
                        "Solo puede ejecutarse manualmente"),
                1,
                QuestionState.BORRADOR));
    }
}
