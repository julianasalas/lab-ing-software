package co.unicauca.bancopreguntas.presentation;

import co.unicauca.bancopreguntas.domain.QuestionState;
import co.unicauca.bancopreguntas.infra.Observer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.Map;

/**
 * Primera vista observadora: estadisticas de cuantas preguntas hay por estado.
 *
 * Capa: presentacion. Rol en Observer: <b>Observador concreto</b>.
 *
 * Se refresca sola cuando el modelo notifica un cambio; la ventana principal
 * nunca la invoca directamente.
 */
public class GUIObserver1 extends JFrame implements Observer {

    private final QuestionController controller;
    private final Map<QuestionState, JLabel> valueLabels = new EnumMap<>(QuestionState.class);
    private final JLabel lblTotal = new JLabel();

    public GUIObserver1(QuestionController controller) {
        super("Vista de estadisticas");
        this.controller = controller;

        initComponents();
        update(); // pinta el estado inicial

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 230);
        setLocation(600, 40);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Preguntas por estado", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(QuestionState.values().length, 2, 6, 6));
        for (QuestionState state : QuestionState.values()) {
            grid.add(new JLabel(state.getLabel() + ":"));
            JLabel value = new JLabel("0");
            value.setFont(value.getFont().deriveFont(Font.BOLD));
            valueLabels.put(state, value);
            grid.add(value);
        }
        panel.add(grid, BorderLayout.CENTER);

        lblTotal.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTotal, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    /** Callback del patron Observer: consulta al modelo y repinta. */
    @Override
    public void update() {
        Map<QuestionState, Integer> statistics = controller.getStatistics();
        for (QuestionState state : QuestionState.values()) {
            valueLabels.get(state).setText(String.valueOf(statistics.getOrDefault(state, 0)));
        }
        lblTotal.setText("Total: " + controller.getTotalQuestions() + " preguntas");
    }
}
