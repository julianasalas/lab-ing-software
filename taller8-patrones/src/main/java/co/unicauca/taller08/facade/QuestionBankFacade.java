package co.unicauca.taller08.facade;

import co.unicauca.taller08.adapter.QuestionProvider;
import co.unicauca.taller08.decorator.BasicQuestion;
import co.unicauca.taller08.decorator.QuestionComponent;
import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import co.unicauca.taller08.service.QuestionImporter;
import co.unicauca.taller08.service.QuestionPublisher;
import co.unicauca.taller08.service.QuestionRepository;
import co.unicauca.taller08.service.QuestionService;
import co.unicauca.taller08.service.QuestionValidator;
import co.unicauca.taller08.templatemethod.EvaluatorFactory;
import co.unicauca.taller08.templatemethod.QuestionEvaluator;
import java.util.List;

/**
 * Patrón Facade: punto de entrada único al Banco de Preguntas.
 *
 * El cliente solo usa esta clase y no necesita conocer los subsistemas
 * (QuestionService, QuestionRepository, QuestionValidator,
 * QuestionEvaluator, QuestionPublisher y QuestionImporter).
 */
public class QuestionBankFacade {

    private final QuestionService questionService;
    private final QuestionValidator validator;
    private final QuestionPublisher publisher;
    private final QuestionImporter importer;
    private final EvaluatorFactory evaluatorFactory;

    private String lastFeedback = "";

    /** Crea la fachada con todos sus subsistemas. */
    public QuestionBankFacade() {
        this(new QuestionRepository());
    }

    private QuestionBankFacade(QuestionRepository repository) {
        this(new QuestionService(repository), new QuestionValidator(), new QuestionPublisher(),
                new QuestionImporter(repository), new EvaluatorFactory());
    }

    /** Permite entregar los subsistemas desde afuera (útil en pruebas). */
    public QuestionBankFacade(QuestionService questionService, QuestionValidator validator,
                              QuestionPublisher publisher, QuestionImporter importer,
                              EvaluatorFactory evaluatorFactory) {
        this.questionService = questionService;
        this.validator = validator;
        this.publisher = publisher;
        this.importer = importer;
        this.evaluatorFactory = evaluatorFactory;
    }

    // ------------------------------------------------------------------
    // Crear y consultar
    // ------------------------------------------------------------------

    /** Valida una pregunta completa y la guarda (queda en Borrador). */
    public Question createQuestion(Question question) {
        validator.validate(question);
        return questionService.save(question);
    }

    /** Crea una pregunta en Borrador para irle agregando opciones después. */
    public Question createDraftQuestion(String statement, QuestionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de pregunta");
        }
        return questionService.save(new Question(statement, type));
    }

    /** Registra una opción de respuesta (solo en Borrador). */
    public Question addOption(Long questionId, String text, boolean correct) {
        Question question = questionService.findById(questionId);
        question.addOption(text, correct);
        return question;
    }

    public Question findQuestion(Long id) {
        return questionService.findById(id);
    }

    public List<Question> listQuestions() {
        return questionService.findAll();
    }

    public List<Question> listQuestionsByState(String stateName) {
        return questionService.findByState(stateName);
    }

    public List<String> getValidationErrors(Long id) {
        return validator.getErrors(questionService.findById(id));
    }

    // ------------------------------------------------------------------
    // Ciclo de vida (los permisos los decide el patrón State)
    // ------------------------------------------------------------------

    public void submitForReview(Long id) {
        questionService.submitForReview(questionService.findById(id));
    }

    public void assignReviewer(Long id, String reviewer) {
        questionService.assignReviewer(questionService.findById(id), reviewer);
    }

    public void approveQuestion(Long id) {
        questionService.approve(questionService.findById(id));
    }

    public void rejectQuestion(Long id, String reason) {
        questionService.reject(questionService.findById(id), reason);
    }

    public void returnToDraft(Long id) {
        questionService.returnToDraft(questionService.findById(id));
    }

    /** Publica una pregunta que ya está Aprobada. */
    public void publishQuestion(Long id) {
        Question question = questionService.findById(id);
        validator.validate(question);
        publisher.publish(question);
    }

    /** Aprueba y publica en un solo paso una pregunta que está En revisión. */
    public void approveAndPublish(Long id) {
        Question question = questionService.findById(id);
        validator.validate(question);
        questionService.approve(question);
        publisher.publish(question);
    }

    public void archiveQuestion(Long id) {
        questionService.archive(questionService.findById(id));
    }

    public List<Question> listAvailableForTests() {
        return publisher.getAvailableForTests();
    }

    // ------------------------------------------------------------------
    // Evaluar (Template Method), decorar (Decorator) e importar (Adapter)
    // ------------------------------------------------------------------

    /** Evalúa la respuesta de un estudiante con el evaluador del tipo de pregunta. */
    public boolean evaluateAnswer(Long id, List<String> answers) {
        Question question = questionService.findById(id);
        QuestionEvaluator evaluator = evaluatorFactory.forType(question.getType());
        boolean result = evaluator.evaluate(question, answers);
        lastFeedback = evaluator.getLastFeedback();
        return result;
    }

    public String getLastFeedback() {
        return lastFeedback;
    }

    /** Devuelve la pregunta como componente para agregarle características con Decorator. */
    public QuestionComponent getQuestionComponent(Long id) {
        return new BasicQuestion(questionService.findById(id));
    }

    /** Importa y guarda una pregunta de una fuente externa a través de un Adapter. */
    public Question importQuestion(QuestionProvider provider) {
        return importer.importFrom(provider);
    }
}
