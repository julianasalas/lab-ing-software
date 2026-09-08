package co.unicauca.bancopreguntas;

import co.unicauca.bancopreguntas.access.QuestionImplRepository;
import co.unicauca.bancopreguntas.domain.QuestionRepository;
import co.unicauca.bancopreguntas.domain.QuestionService;
import co.unicauca.bancopreguntas.presentation.GUIObserver1;
import co.unicauca.bancopreguntas.presentation.GUIObserver2;
import co.unicauca.bancopreguntas.presentation.GUIQuestions;
import co.unicauca.bancopreguntas.presentation.QuestionController;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicacion.
 *
 * Es el unico lugar donde se decide que implementacion concreta se inyecta:
 * las demas clases solo conocen abstracciones. Cambiar el repositorio en memoria
 * por uno con base de datos se resuelve cambiando una sola linea de este metodo.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::start);
    }

    private static void start() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Si el look and feel del sistema no esta disponible se usa el por defecto.
        }

        // 1. Capa de acceso a datos.
        QuestionRepository repository = new QuestionImplRepository();

        // 2. Capa de dominio (Modelo / Sujeto observable).
        QuestionService service = new QuestionService(repository);

        // 3. Capa de presentacion (Controlador y Vistas).
        QuestionController controller = new QuestionController(service);

        GUIQuestions mainWindow = new GUIQuestions(controller);
        GUIObserver1 statisticsView = new GUIObserver1(controller);
        GUIObserver2 chartView = new GUIObserver2(controller);

        // 4. Suscripcion de las vistas al sujeto: a partir de aqui, cada cambio
        //    de estado refresca ambas ventanas automaticamente.
        service.attach(statisticsView);
        service.attach(chartView);

        mainWindow.setVisible(true);
        statisticsView.setVisible(true);
        chartView.setVisible(true);
    }
}
