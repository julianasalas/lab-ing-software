package co.unicauca.taller08;

import co.unicauca.taller08.adapter.ExternalQuestionAdapter;
import co.unicauca.taller08.adapter.ExternalQuestionService;
import co.unicauca.taller08.decorator.CompetencyQuestion;
import co.unicauca.taller08.decorator.ContextualizedQuestion;
import co.unicauca.taller08.decorator.FeedbackQuestion;
import co.unicauca.taller08.decorator.HighDifficultyQuestion;
import co.unicauca.taller08.decorator.MarkedForReviewQuestion;
import co.unicauca.taller08.decorator.QuestionComponent;
import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import co.unicauca.taller08.facade.QuestionBankFacade;
import co.unicauca.taller08.service.QuestionValidator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Cliente del Banco de Preguntas Saber PRO.
 *
 * Toda la interacción pasa por la fachada QuestionBankFacade.
 * Arranca con algunas preguntas de ejemplo y muestra un menú en consola.
 */
public class ApplicationMain {

    private static final QuestionBankFacade facade = new QuestionBankFacade();
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        loadSampleData();
        System.out.println("=== BANCO DE PREGUNTAS SABER PRO - Taller 8 ===");
        try {
            boolean running = true;
            while (running) {
                printMenu();
                String option = read("Opción: ");
                try {
                    switch (option) {
                        case "1" -> createQuestion();
                        case "2" -> listQuestions();
                        case "3" -> addOption();
                        case "4" -> changeState();
                        case "5" -> evaluate();
                        case "6" -> decorate();
                        case "7" -> importExternal();
                        case "8" -> runDemo();
                        case "0" -> running = false;
                        default -> System.out.println("Opción no válida.");
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println(">> " + e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            // Se terminó la entrada (por ejemplo, al ejecutar sin consola)
        }
        System.out.println("Hasta luego.");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Crear pregunta");
        System.out.println("2. Consultar preguntas");
        System.out.println("3. Agregar opción a una pregunta en Borrador");
        System.out.println("4. Cambiar el estado de una pregunta (State)");
        System.out.println("5. Evaluar una respuesta (Template Method)");
        System.out.println("6. Agregar características a una pregunta (Decorator)");
        System.out.println("7. Importar pregunta externa (Adapter)");
        System.out.println("8. Ver demostración completa");
        System.out.println("0. Salir");
    }

    // ------------------------------------------------------------------
    // Opciones del menú
    // ------------------------------------------------------------------

    private static void createQuestion() {
        System.out.println("Tipo de pregunta:");
        QuestionType[] types = QuestionType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println("  " + (i + 1) + ". " + types[i].getDisplayName());
        }
        int index = Integer.parseInt(read("Tipo: ")) - 1;
        if (index < 0 || index >= types.length) {
            throw new IllegalArgumentException("Tipo no válido");
        }
        QuestionType type = types[index];
        Question question = new Question(read("Enunciado: "), type);

        if (type == QuestionType.TRUE_FALSE) {
            boolean isTrue = read("¿La afirmación es verdadera? (s/n): ").equalsIgnoreCase("s");
            QuestionValidator.addTrueFalseOptions(question, isTrue);
        } else {
            int count = Integer.parseInt(read("¿Cuántas opciones? "));
            for (int i = 0; i < count; i++) {
                char label = (char) ('A' + i);
                String text = read("  Opción " + label + ": ");
                boolean correct = read("  ¿Es correcta? (s/n): ").equalsIgnoreCase("s");
                question.addOption(text, correct);
            }
        }
        facade.createQuestion(question);
        System.out.println("Pregunta creada en Borrador:");
        System.out.println(question);
    }

    private static void listQuestions() {
        List<Question> questions = facade.listQuestions();
        if (questions.isEmpty()) {
            System.out.println("No hay preguntas.");
        }
        for (Question q : questions) {
            System.out.println(q);
        }
    }

    private static void addOption() {
        Long id = readId();
        String text = read("Texto de la opción: ");
        boolean correct = read("¿Es correcta? (s/n): ").equalsIgnoreCase("s");
        System.out.println(facade.addOption(id, text, correct));
    }

    private static void changeState() {
        Long id = readId();
        System.out.println("Estado actual: " + facade.findQuestion(id).getStateName());
        System.out.println("  1. Enviar a revisión     2. Asignar revisor    3. Aprobar");
        System.out.println("  4. Rechazar              5. Devolver a borrador 6. Publicar");
        System.out.println("  7. Archivar");
        switch (read("Operación: ")) {
            case "1" -> facade.submitForReview(id);
            case "2" -> facade.assignReviewer(id, read("Revisor: "));
            case "3" -> facade.approveQuestion(id);
            case "4" -> facade.rejectQuestion(id, read("Motivo: "));
            case "5" -> facade.returnToDraft(id);
            case "6" -> facade.publishQuestion(id);
            case "7" -> facade.archiveQuestion(id);
            default -> throw new IllegalArgumentException("Operación no válida");
        }
        System.out.println("Nuevo estado: " + facade.findQuestion(id).getStateName());
    }

    private static void evaluate() {
        Long id = readId();
        System.out.println(facade.findQuestion(id));
        String line = read("Respuesta(s) separadas por coma (ej: B  o  A,C  o  V): ");
        List<String> answers = new ArrayList<>();
        for (String part : line.split(",")) {
            if (!part.isBlank()) {
                answers.add(part.trim());
            }
        }
        boolean ok = facade.evaluateAnswer(id, answers);
        System.out.println(ok ? "CORRECTA" : "INCORRECTA");
        System.out.println(facade.getLastFeedback());
    }

    private static void decorate() {
        Long id = readId();
        QuestionComponent component = facade.getQuestionComponent(id);
        System.out.println("Características (separadas por coma):");
        System.out.println("  1. Alta dificultad  2. Contextualizada  3. Con retroalimentación");
        System.out.println("  4. Asociada a competencia  5. Marcada para revisión");
        for (String choice : read("Características: ").split(",")) {
            switch (choice.trim()) {
                case "1" -> component = new HighDifficultyQuestion(component);
                case "2" -> component = new ContextualizedQuestion(component, read("Contexto: "));
                case "3" -> component = new FeedbackQuestion(component, read("Retroalimentación: "));
                case "4" -> component = new CompetencyQuestion(component, read("Competencia: "));
                case "5" -> component = new MarkedForReviewQuestion(component);
                default -> System.out.println("Se ignora la opción " + choice.trim());
            }
        }
        System.out.println("Descripción: " + component.getDescription());
        System.out.println("Puntaje: " + component.getScore());
    }

    private static void importExternal() {
        Question q = facade.importQuestion(new ExternalQuestionAdapter(new ExternalQuestionService()));
        System.out.println("Pregunta importada desde el servicio externo (queda en Borrador):");
        System.out.println(q);
        System.out.println("Agregue las demás opciones con la opción 3 antes de enviarla a revisión.");
    }

    // ------------------------------------------------------------------
    // Demostración completa (no pide datos)
    // ------------------------------------------------------------------

    private static void runDemo() {
        title("1-4. Crear preguntas de los tres tipos, con opciones y respuestas correctas");
        Question single = new Question("Si un artículo cuesta $80.000 y tiene 25% de descuento, ¿cuánto se paga?",
                QuestionType.SINGLE_CHOICE);
        single.addOption("$55.000", false);
        single.addOption("$60.000", true);
        single.addOption("$65.000", false);
        facade.createQuestion(single);

        Question multiple = new Question("¿Cuáles de los siguientes son números primos?", QuestionType.MULTIPLE_CHOICE);
        multiple.addOption("2", true);
        multiple.addOption("4", false);
        multiple.addOption("7", true);
        multiple.addOption("9", false);
        facade.createQuestion(multiple);

        Question trueFalse = new Question("La tesis es la idea principal que defiende un texto argumentativo.",
                QuestionType.TRUE_FALSE);
        QuestionValidator.addTrueFalseOptions(trueFalse, true);
        facade.createQuestion(trueFalse);
        System.out.println(single + "\n" + multiple + "\n" + trueFalse);

        title("6. Ciclo de vida (State)");
        Long id = single.getId();
        tryStep("Borrador → Publicada (no permitido)", () -> facade.publishQuestion(id));
        tryStep("Borrador → Pendiente de revisión", () -> facade.submitForReview(id));
        tryStep("Pendiente de revisión → En revisión", () -> facade.assignReviewer(id, "Docente revisor"));
        tryStep("En revisión → Aprobada", () -> facade.approveQuestion(id));
        tryStep("Aprobada → Publicada", () -> facade.publishQuestion(id));
        tryStep("Publicada → Borrador (no permitido)", () -> facade.returnToDraft(id));
        System.out.println("Historial: " + single.getStateHistory());

        System.out.println("\nRechazo y corrección:");
        Long tf = trueFalse.getId();
        tryStep("Enviar a revisión", () -> facade.submitForReview(tf));
        tryStep("Asignar revisor", () -> facade.assignReviewer(tf, "Docente revisor"));
        tryStep("Rechazar", () -> facade.rejectQuestion(tf, "Mejorar la redacción"));
        tryStep("Volver a Borrador", () -> facade.returnToDraft(tf));
        tryStep("Enviar de nuevo, asignar revisor y aprobar/publicar", () -> {
            facade.submitForReview(tf);
            facade.assignReviewer(tf, "Docente revisor");
            facade.approveAndPublish(tf);
        });
        System.out.println("Historial: " + trueFalse.getStateHistory());

        title("7. Evaluar respuestas (Template Method)");
        Long mc = multiple.getId();
        tryStep("Evaluar pregunta que NO está publicada", () -> facade.evaluateAnswer(mc, List.of("A")));
        facade.submitForReview(mc);
        facade.assignReviewer(mc, "Docente revisor");
        facade.approveAndPublish(mc);
        showEvaluation(id, List.of("B"));
        showEvaluation(id, List.of("$55.000"));
        showEvaluation(mc, Arrays.asList("A", "C"));
        showEvaluation(mc, List.of("A"));
        showEvaluation(tf, List.of("V"));
        tryStep("Única respuesta con dos opciones marcadas", () -> facade.evaluateAnswer(id, List.of("A", "B")));

        title("8. Agregar características (Decorator)");
        QuestionComponent component = facade.getQuestionComponent(id);
        System.out.println(component.getDescription() + "  | puntaje " + component.getScore());
        component = new HighDifficultyQuestion(component);
        component = new CompetencyQuestion(component, "Razonamiento cuantitativo");
        component = new FeedbackQuestion(component, "25% de 80.000 es 20.000");
        System.out.println(component.getDescription() + "  | puntaje " + component.getScore());

        title("9. Importar pregunta externa (Adapter)");
        Question imported = facade.importQuestion(new ExternalQuestionAdapter(new ExternalQuestionService()));
        System.out.println(imported);
        facade.addOption(imported.getId(), "Medellín", false);
        facade.addOption(imported.getId(), "Cali", false);
        System.out.println("Con las opciones completadas:\n" + imported);

        title("5. Consultar preguntas");
        for (Question q : facade.listQuestions()) {
            System.out.println(q);
        }
        System.out.println("\nDisponibles para pruebas: " + facade.listAvailableForTests().size());
    }

    private static void showEvaluation(Long id, List<String> answers) {
        boolean ok = facade.evaluateAnswer(id, answers);
        System.out.println("Pregunta #" + id + " respuesta " + answers + " -> "
                + (ok ? "CORRECTA" : "INCORRECTA") + ". " + facade.getLastFeedback());
    }

    private static void tryStep(String description, Runnable action) {
        try {
            action.run();
            System.out.println("  OK  " + description);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("  NO  " + description + " -> " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    private static void loadSampleData() {
        Question q = new Question("¿Qué patrón permite agregar responsabilidades a un objeto dinámicamente?",
                QuestionType.SINGLE_CHOICE);
        q.addOption("Facade", false);
        q.addOption("Decorator", true);
        q.addOption("Singleton", false);
        facade.createQuestion(q);
        facade.submitForReview(q.getId());
        facade.assignReviewer(q.getId(), "Revisor de ejemplo");
        facade.approveAndPublish(q.getId());
    }

    private static void title(String text) {
        System.out.println("\n--- " + text + " ---");
    }

    private static String read(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    private static Long readId() {
        try {
            return Long.parseLong(read("Id de la pregunta: "));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El id debe ser un número");
        }
    }
}
