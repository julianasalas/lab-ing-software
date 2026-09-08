package co.unicauca.bancopreguntas.presentation;

import co.unicauca.bancopreguntas.domain.QuestionState;
import co.unicauca.bancopreguntas.infra.Observer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.EnumMap;
import java.util.Map;

/**
 * Segunda vista observadora: grafica de pastel con el porcentaje de preguntas
 * por estado.
 *
 * Capa: presentacion. Rol en Observer: <b>Observador concreto</b>.
 *
 * La grafica se dibuja con Java2D, sin librerias externas.
 */
public class GUIObserver2 extends JFrame implements Observer {

    private static final Map<QuestionState, Color> COLORS = new EnumMap<>(QuestionState.class);

    static {
        COLORS.put(QuestionState.BORRADOR, new Color(0x9E, 0x9E, 0x9E));
        COLORS.put(QuestionState.PENDIENTE_REVISION, new Color(0xF5, 0xA6, 0x23));
        COLORS.put(QuestionState.ELIMINADA, new Color(0xD1, 0x4A, 0x3F));
    }

    private final QuestionController controller;
    private final PieChartPanel chartPanel = new PieChartPanel();
    private final Map<QuestionState, JLabel> legendLabels = new EnumMap<>(QuestionState.class);

    private Map<QuestionState, Double> percentages = new EnumMap<>(QuestionState.class);

    public GUIObserver2(QuestionController controller) {
        super("Vista grafica - Distribucion de preguntas");
        this.controller = controller;

        initComponents();
        update(); // pinta el estado inicial

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(380, 420);
        setLocation(600, 300);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Distribucion de preguntas", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(title, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        JPanel legend = new JPanel(new GridLayout(QuestionState.values().length, 1, 4, 4));
        for (QuestionState state : QuestionState.values()) {
            JLabel label = new JLabel(state.getLabel() + ": 0,0%");
            label.setForeground(COLORS.get(state).darker());
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            legendLabels.put(state, label);
            legend.add(label);
        }
        panel.add(legend, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    /** Callback del patron Observer: recalcula porcentajes y repinta el pastel. */
    @Override
    public void update() {
        percentages = controller.getPercentages();
        for (QuestionState state : QuestionState.values()) {
            double value = percentages.getOrDefault(state, 0.0);
            legendLabels.get(state).setText(
                    String.format("%s: %.1f%%", state.getLabel(), value));
        }
        chartPanel.repaint();
    }

    /** Panel que dibuja el pastel con Java2D. */
    private class PieChartPanel extends JPanel {

        PieChartPanel() {
            setPreferredSize(new Dimension(300, 240));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2d = (Graphics2D) graphics.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 30;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            double total = percentages.values().stream().mapToDouble(Double::doubleValue).sum();
            if (total <= 0.0) {
                g2d.setColor(Color.GRAY);
                g2d.drawOval(x, y, size, size);
                g2d.drawString("Sin preguntas", getWidth() / 2 - 40, getHeight() / 2);
                g2d.dispose();
                return;
            }

            double startAngle = 90.0; // arranca arriba
            for (QuestionState state : QuestionState.values()) {
                double percentage = percentages.getOrDefault(state, 0.0);
                if (percentage <= 0.0) {
                    continue;
                }
                double arcAngle = -(percentage * 360.0) / 100.0; // sentido horario
                g2d.setColor(COLORS.get(state));
                g2d.fillArc(x, y, size, size,
                            (int) Math.round(startAngle),
                            (int) Math.round(arcAngle));
                startAngle += arcAngle;
            }

            g2d.setColor(Color.WHITE);
            g2d.drawOval(x, y, size, size);
            g2d.dispose();
        }
    }
}
