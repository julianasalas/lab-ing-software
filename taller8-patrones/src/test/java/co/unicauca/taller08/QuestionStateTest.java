package co.unicauca.taller08;

import static org.junit.jupiter.api.Assertions.*;

import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import co.unicauca.taller08.state.ApprovedState;
import co.unicauca.taller08.state.ArchivedState;
import co.unicauca.taller08.state.DraftState;
import co.unicauca.taller08.state.PendingReviewState;
import co.unicauca.taller08.state.PublishedState;
import co.unicauca.taller08.state.QuestionState;
import co.unicauca.taller08.state.RejectedState;
import co.unicauca.taller08.state.ReviewState;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas del patrón State: ciclo de vida de una pregunta (Figura 1 y Tabla 2).
 */
@DisplayName("State - ciclo de vida de la pregunta")
class QuestionStateTest {

    /** Operaciones del ciclo de vida, para probarlas todas en cada estado. */
    enum Op {
        SUBMIT(q -> q.submitForReview()),
        ASSIGN(q -> q.assignReviewer("Revisor")),
        APPROVE(Question::approve),
        REJECT(q -> q.reject("Motivo")),
        RETURN_TO_DRAFT(Question::returnToDraft),
        PUBLISH(Question::publish),
        ARCHIVE(Question::archive);

        final Consumer<Question> action;

        Op(Consumer<Question> action) {
            this.action = action;
        }
    }

    /** Crea una pregunta válida que está en el estado pedido. */
    static Question inState(String state) {
        Question q = TestQuestions.singleChoice();
        switch (state) {
            case "Borrador" -> { }
            case "Pendiente de revisión" -> q.submitForReview();
            case "En revisión" -> TestQuestions.toReview(q);
            case "Rechazada" -> { TestQuestions.toReview(q); q.reject("Motivo"); }
            case "Aprobada" -> { TestQuestions.toReview(q); q.approve(); }
            case "Publicada" -> TestQuestions.publish(q);
            case "Archivada" -> { TestQuestions.publish(q); q.archive(); }
            default -> throw new IllegalArgumentException(state);
        }
        assertEquals(state, q.getStateName());
        return q;
    }

    /** Estado, operación permitida y estado al que llega. */
    static Stream<Arguments> allowedTransitions() {
        return Stream.of(
                Arguments.of("Borrador", Op.SUBMIT, "Pendiente de revisión"),
                Arguments.of("Pendiente de revisión", Op.ASSIGN, "En revisión"),
                Arguments.of("En revisión", Op.APPROVE, "Aprobada"),
                Arguments.of("En revisión", Op.REJECT, "Rechazada"),
                Arguments.of("Rechazada", Op.RETURN_TO_DRAFT, "Borrador"),
                Arguments.of("Aprobada", Op.PUBLISH, "Publicada"),
                Arguments.of("Publicada", Op.ARCHIVE, "Archivada"));
    }

    /** Todas las combinaciones estado + operación que NO están permitidas. */
    static Stream<Arguments> forbiddenTransitions() {
        List<String> states = List.of("Borrador", "Pendiente de revisión", "En revisión", "Rechazada",
                "Aprobada", "Publicada", "Archivada");
        List<Arguments> allowed = allowedTransitions().toList();
        Stream.Builder<Arguments> result = Stream.builder();
        for (String s : states) {
            for (Op op : Op.values()) {
                boolean isAllowed = allowed.stream()
                        .anyMatch(a -> a.get()[0].equals(s) && a.get()[1] == op);
                if (!isAllowed) {
                    result.add(Arguments.of(s, op));
                }
            }
        }
        return result.build();
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Tabla 2 de la guía")
    class Tabla2 {

        @Test
        @DisplayName("1. Borrador → En revisión: permitido (pasando por Pendiente de revisión)")
        void draftToReview() {
            Question q = TestQuestions.singleChoice();
            q.submitForReview();
            q.assignReviewer("Revisor");
            assertEquals("En revisión", q.getStateName());
        }

        @Test
        @DisplayName("2. En revisión → Aprobada: permitido")
        void reviewToApproved() {
            Question q = inState("En revisión");
            q.approve();
            assertEquals("Aprobada", q.getStateName());
        }

        @Test
        @DisplayName("3. Aprobada → Publicada: permitido")
        void approvedToPublished() {
            Question q = inState("Aprobada");
            q.publish();
            assertEquals("Publicada", q.getStateName());
        }

        @Test
        @DisplayName("4. Borrador → Publicada: NO permitido")
        void draftToPublishedNotAllowed() {
            Question q = TestQuestions.singleChoice();
            IllegalStateException e = assertThrows(IllegalStateException.class, q::publish);
            assertTrue(e.getMessage().contains("no se puede publicar"));
            assertEquals("Borrador", q.getStateName());
        }

        @Test
        @DisplayName("5. Publicada → Borrador: NO permitido")
        void publishedToDraftNotAllowed() {
            Question q = inState("Publicada");
            assertThrows(IllegalStateException.class, q::returnToDraft);
            assertEquals("Publicada", q.getStateName());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Transiciones")
    class Transiciones {

        @ParameterizedTest(name = "{0} --{1}--> {2}")
        @MethodSource("co.unicauca.taller08.QuestionStateTest#allowedTransitions")
        @DisplayName("Transiciones permitidas de la Figura 1")
        void allowed(String from, Op op, String to) {
            Question q = inState(from);
            op.action.accept(q);
            assertEquals(to, q.getStateName());
        }

        @ParameterizedTest(name = "{0} no permite {1}")
        @MethodSource("co.unicauca.taller08.QuestionStateTest#forbiddenTransitions")
        @DisplayName("Toda operación fuera de la Figura 1 se rechaza y no cambia el estado")
        void forbidden(String from, Op op) {
            Question q = inState(from);
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> op.action.accept(q));
            assertTrue(e.getMessage().startsWith("Operación no permitida"));
            assertTrue(e.getMessage().contains(from));
            assertEquals(from, q.getStateName());
        }

        @Test
        @DisplayName("Ciclo completo: Borrador → … → Archivada queda en el historial")
        void fullLifecycleHistory() {
            Question q = TestQuestions.publish(TestQuestions.singleChoice());
            q.archive();
            assertIterableEquals(List.of("Borrador", "Pendiente de revisión", "En revisión",
                    "Aprobada", "Publicada", "Archivada"), q.getStateHistory());
        }

        @Test
        @DisplayName("Rechazada vuelve a Borrador y puede volver a enviarse")
        void rejectionLoop() {
            Question q = inState("Rechazada");
            q.returnToDraft();
            q.submitForReview();
            q.assignReviewer("Otro revisor");
            q.approve();
            assertEquals("Aprobada", q.getStateName());
            assertEquals("Otro revisor", q.getReviewer());
        }

        @Test
        @DisplayName("Una pregunta nueva empieza en Borrador")
        void startsInDraft() {
            Question q = new Question();
            assertInstanceOf(DraftState.class, q.getState());
            assertEquals("Borrador", q.getStateName());
        }

        @Test
        @DisplayName("Cada transición deja el objeto de estado concreto correcto")
        void concreteStateClasses() {
            Question q = TestQuestions.singleChoice();
            q.submitForReview();
            assertInstanceOf(PendingReviewState.class, q.getState());
            q.assignReviewer("R");
            assertInstanceOf(ReviewState.class, q.getState());
            q.approve();
            assertInstanceOf(ApprovedState.class, q.getState());
            q.publish();
            assertInstanceOf(PublishedState.class, q.getState());
            q.archive();
            assertInstanceOf(ArchivedState.class, q.getState());
        }

        @Test
        @DisplayName("Rechazar deja el estado RejectedState")
        void rejectedStateClass() {
            Question q = inState("En revisión");
            q.reject("Mala redacción");
            assertInstanceOf(RejectedState.class, q.getState());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Reglas de cada estado")
    class Reglas {

        @Test
        @DisplayName("No se puede enviar a revisión una pregunta incompleta")
        void incompleteCannotBeSubmitted() {
            Question q = new Question("¿Pregunta sin opciones todavía?", QuestionType.SINGLE_CHOICE);
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, q::submitForReview);
            assertTrue(e.getMessage().contains("opciones"));
            assertEquals("Borrador", q.getStateName());
        }

        @ParameterizedTest(name = "revisor [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Asignar revisor exige un nombre")
        void reviewerRequired(String reviewer) {
            Question q = inState("Pendiente de revisión");
            assertThrows(IllegalArgumentException.class, () -> q.assignReviewer(reviewer));
            assertEquals("Pendiente de revisión", q.getStateName());
        }

        @ParameterizedTest(name = "motivo [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("Rechazar exige un motivo")
        void reasonRequired(String reason) {
            Question q = inState("En revisión");
            assertThrows(IllegalArgumentException.class, () -> q.reject(reason));
            assertEquals("En revisión", q.getStateName());
        }

        @Test
        @DisplayName("Se guardan el revisor y el motivo del rechazo")
        void keepsReviewerAndReason() {
            Question q = inState("En revisión");
            q.reject("Faltan distractores");
            assertEquals("Revisor", q.getReviewer());
            assertEquals("Faltan distractores", q.getRejectionReason());
        }

        @Test
        @DisplayName("Al volver a Borrador se limpia el revisor; al reenviar se limpia el motivo")
        void cleansAfterRejection() {
            Question q = inState("Rechazada");
            q.returnToDraft();
            assertNull(q.getReviewer());
            q.submitForReview();
            assertNull(q.getRejectionReason());
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {"Pendiente de revisión", "En revisión", "Rechazada", "Aprobada", "Publicada", "Archivada"})
        @DisplayName("Solo en Borrador se puede editar el contenido")
        void onlyDraftIsEditable(String state) {
            Question q = inState(state);
            assertFalse(q.isEditable());
            assertThrows(IllegalStateException.class, () -> q.addOption("Nueva", false));
            assertThrows(IllegalStateException.class, () -> q.setStatement("Otro enunciado distinto"));
            assertThrows(IllegalStateException.class, () -> q.setType(QuestionType.TRUE_FALSE));
            assertThrows(IllegalStateException.class, q::clearOptions);
        }

        @Test
        @DisplayName("En Borrador sí se puede editar")
        void draftIsEditable() {
            Question q = TestQuestions.singleChoice();
            assertTrue(q.isEditable());
            assertDoesNotThrow(() -> q.addOption("6", false));
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {"Borrador", "Pendiente de revisión", "En revisión", "Rechazada", "Aprobada", "Archivada"})
        @DisplayName("Solo una pregunta Publicada se puede usar en pruebas")
        void onlyPublishedAvailable(String state) {
            assertFalse(inState(state).isAvailableForTests());
        }

        @Test
        @DisplayName("Una pregunta Publicada sí está disponible para pruebas")
        void publishedAvailable() {
            assertTrue(inState("Publicada").isAvailableForTests());
        }

        @Test
        @DisplayName("No se puede asignar un estado null")
        void nullStateRejected() {
            assertThrows(IllegalArgumentException.class, () -> new Question().setState(null));
        }

        @Test
        @DisplayName("Todos los estados tienen nombre")
        void statesHaveNames() {
            List<QuestionState> states = List.of(new DraftState(), new PendingReviewState(), new ReviewState(),
                    new RejectedState(), new ApprovedState(), new PublishedState(), new ArchivedState());
            for (QuestionState s : states) {
                assertFalse(s.getName().isBlank());
                assertEquals(s.getName(), s.toString());
            }
        }
    }
}
