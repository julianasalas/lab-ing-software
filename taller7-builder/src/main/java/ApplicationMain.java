import builder.MultipleChoiceQuestionBuilder;
import builder.OpenQuestionBuilder;
import builder.QuestionBuilder;
import builder.TrueFalseQuestionBuilder;
import director.QuestionDirector;
import domain.DifficultyLevel;
import domain.Question;
import java.util.List;

/**
 * Cliente del patrón Builder.
 *
 * a) Crea un QuestionDirector.
 * b) Crea los diferentes Builders.
 * c) Usa el Director para construir preguntas.
 * d) Muestra en consola las preguntas construidas.
 */
public class ApplicationMain {

    public static void main(String[] args) {
        // a) Director
        QuestionDirector director = new QuestionDirector();

        // b) Builders concretos
        QuestionBuilder multipleChoiceBuilder = new MultipleChoiceQuestionBuilder();
        QuestionBuilder trueFalseBuilder = new TrueFalseQuestionBuilder();
        QuestionBuilder openBuilder = new OpenQuestionBuilder();

        System.out.println("=== BANCO DE PREGUNTAS SABER PRO - PATRÓN BUILDER ===\n");

        // c) El mismo Director construye distintos tipos de pregunta
        director.setBuilder(multipleChoiceBuilder);
        Question multipleChoice = director.construct(
                "Si un artículo cuesta $80.000 y tiene un descuento del 25%, ¿cuánto se paga?",
                DifficultyLevel.LOW,
                "Razonamiento cuantitativo",
                "Juliana Salas",
                "El 25% de 80.000 es 20.000, por lo que se pagan 80.000 - 20.000 = 60.000.",
                "Guía de orientación Saber Pro - ICFES",
                List.of("$55.000", "$60.000", "$65.000", "$70.000"),
                "B");

        director.setBuilder(trueFalseBuilder);
        Question trueFalse = director.construct(
                "En un texto argumentativo la tesis es la idea principal que el autor defiende.",
                DifficultyLevel.MEDIUM,
                "Lectura crítica",
                "Juliana Salas",
                "La tesis es la postura central que el autor sostiene con argumentos.",
                null,
                List.of(),
                "Verdadero");

        director.setBuilder(openBuilder);
        Question open = director.constructWithoutSource(
                "Explique por qué el voto es un mecanismo de participación ciudadana.",
                DifficultyLevel.HIGH,
                "Competencias ciudadanas",
                "Juliana Salas",
                "Se espera que el estudiante relacione el voto con la Constitución de 1991.",
                null,
                "Porque permite a los ciudadanos elegir representantes y decidir sobre asuntos públicos.");

        // d) Mostrar las preguntas construidas
        print("1. Pregunta de selección múltiple", multipleChoice);
        print("2. Pregunta Verdadero / Falso", trueFalse);
        print("3. Pregunta abierta", open);

        // Demostración de las reglas de negocio: una pregunta inválida no se construye
        System.out.println("\n=== VALIDACIÓN DE REGLAS DE NEGOCIO ===");
        director.setBuilder(multipleChoiceBuilder);
        try {
            director.construct(
                    "¿Cuál es la capital de Colombia?",
                    DifficultyLevel.LOW, "Competencias ciudadanas", "Juliana Salas",
                    "Bogotá es la capital según la Constitución.", null,
                    List.of("Bogotá", "Medellín", "Cali"),
                    "E");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se construyó la pregunta: " + e.getMessage());
        }
    }

    private static void print(String title, Question question) {
        System.out.println(title);
        System.out.println(question);
        System.out.println();
    }
}
