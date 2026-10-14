package co.unicauca.taller08.service;

import co.unicauca.taller08.adapter.QuestionProvider;
import co.unicauca.taller08.domain.Question;

/**
 * Subsistema que importa preguntas de fuentes externas. Trabaja con la
 * interfaz QuestionProvider, por eso puede recibir cualquier Adapter.
 */
public class QuestionImporter {

    private final QuestionRepository repository;

    public QuestionImporter(QuestionRepository repository) {
        this.repository = repository;
    }

    public Question importFrom(QuestionProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("El proveedor no puede ser null");
        }
        Question question = provider.getQuestion();
        return repository.save(question);
    }
}
