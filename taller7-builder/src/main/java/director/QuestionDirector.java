package director;

import builder.QuestionBuilder;
import domain.DifficultyLevel;
import domain.Question;
import java.util.List;

/**
 * Director del patrón Builder.
 *
 * Orquesta la construcción de una pregunta definiendo el ORDEN de los pasos.
 * Trabaja únicamente con la abstracción {@link QuestionBuilder}, por lo que no
 * conoce los detalles internos de cada tipo de pregunta: el mismo proceso
 * produce preguntas de selección múltiple, V/F o abiertas según el builder.
 */
public class QuestionDirector {

    private QuestionBuilder builder;

    public QuestionDirector() {
    }

    public QuestionDirector(QuestionBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("El builder no puede ser null");
        }
        this.builder = builder;
    }

    public void setBuilder(QuestionBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("El builder no puede ser null");
        }
        this.builder = builder;
    }

    public QuestionBuilder getBuilder() {
        return builder;
    }

    /**
     * Construye una pregunta completa siguiendo siempre el mismo orden:
     * reset, enunciado, dificultad, competencia, autor, justificación, fuente,
     * opciones, respuesta correcta y finalmente build.
     */
    public Question construct(String statement, DifficultyLevel difficulty, String category,
                              String author, String justification, String source,
                              List<String> options, String correctAnswer) {
        if (builder == null) {
            throw new IllegalStateException("Debe asignar un builder antes de construir");
        }
        builder.reset();
        builder.buildStatement(statement);
        builder.buildDifficulty(difficulty);
        builder.buildCategory(category);
        builder.buildAuthor(author);
        builder.buildJustification(justification);
        builder.buildSource(source);
        builder.buildOptions(options);
        builder.buildCorrectAnswer(correctAnswer);
        return builder.build();
    }

    /** Variante del proceso para preguntas que no tienen fuente bibliográfica. */
    public Question constructWithoutSource(String statement, DifficultyLevel difficulty, String category,
                                           String author, String justification,
                                           List<String> options, String correctAnswer) {
        return construct(statement, difficulty, category, author, justification, null,
                options, correctAnswer);
    }
}
