package co.unicauca.bancopreguntas.domain;

import co.unicauca.bancopreguntas.access.QuestionImplRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de la implementacion del repositorio en memoria.
 */
class QuestionImplRepositoryTest {

    private static Question newQuestion(String id, QuestionState state) {
        return new Question(id, "Pregunta " + id, "Enunciado " + id,
                new QuestionDistractors("A", "B", "C", "D"), 0, state);
    }

    @Test
    @DisplayName("el repositorio arranca con datos de ejemplo")
    void cargaDatosDeEjemplo() {
        QuestionRepository repository = new QuestionImplRepository();

        assertFalse(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("puede arrancar vacio para pruebas")
    void puedeArrancarVacio() {
        QuestionRepository repository = new QuestionImplRepository(false);

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("save guarda una pregunta y findById la recupera")
    void saveYFindById() {
        QuestionRepository repository = new QuestionImplRepository(false);
        repository.save(newQuestion("P-100", QuestionState.BORRADOR));

        assertTrue(repository.findById("P-100").isPresent());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    @DisplayName("save sobre un id existente actualiza en lugar de duplicar")
    void saveActualizaSinDuplicar() {
        QuestionRepository repository = new QuestionImplRepository(false);
        repository.save(newQuestion("P-100", QuestionState.BORRADOR));
        repository.save(newQuestion("P-100", QuestionState.ELIMINADA));

        assertEquals(1, repository.findAll().size());
        assertEquals(QuestionState.ELIMINADA,
                repository.findById("P-100").orElseThrow().getState());
    }

    @Test
    @DisplayName("findById devuelve vacio si el id no existe o es nulo")
    void findByIdInexistente() {
        QuestionRepository repository = new QuestionImplRepository(false);

        assertTrue(repository.findById("NO-EXISTE").isEmpty());
        assertTrue(repository.findById(null).isEmpty());
    }

    @Test
    @DisplayName("save rechaza una pregunta nula")
    void saveRechazaNulo() {
        QuestionRepository repository = new QuestionImplRepository(false);

        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    @DisplayName("findAll devuelve una copia: modificarla no altera el repositorio")
    void findAllDevuelveCopia() {
        QuestionRepository repository = new QuestionImplRepository(false);
        repository.save(newQuestion("P-100", QuestionState.BORRADOR));

        repository.findAll().clear();

        assertEquals(1, repository.findAll().size());
    }
}
