package co.unicauca.bancopreguntas.presentation;

import co.unicauca.bancopreguntas.domain.Question;
import co.unicauca.bancopreguntas.domain.QuestionDistractors;
import co.unicauca.bancopreguntas.domain.QuestionState;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Optional;

/**
 * Ventana principal: seleccion de pregunta, formulario y cambio de estado.
 *
 * Capa: presentacion. Rol en MVC: <b>Vista</b> (y disparador de acciones).
 *
 * No contiene reglas de negocio: delega todo en {@link QuestionController}.
 */
public class GUIQuestions extends JFrame {

    private final QuestionController controller;

    private final JComboBox<Question> cmbQuestions = new JComboBox<>();
    private final JButton btnLoad = new JButton("Cargar pregunta");

    private final JLabel lblId = new JLabel("-");
    private final JLabel lblName = new JLabel("-");
    private final JTextArea txtStatement = new JTextArea(3, 30);
    private final JTextArea txtOptions = new JTextArea(4, 30);
    private final JLabel lblCorrect = new JLabel("-");
    private final JLabel lblCurrentState = new JLabel("-");

    private final JComboBox<QuestionState> cmbNewState = new JComboBox<>(QuestionState.values());
    private final JButton btnUpdateState = new JButton("Actualizar estado");

    private Question selectedQuestion;

    public GUIQuestions(QuestionController controller) {
        super("Banco de Preguntas Saber PRO - Gestion de preguntas");
        this.controller = controller;

        initComponents();
        loadQuestionsIntoCombo();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocation(40, 40);
    }

    private void initComponents() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("GESTION DE PREGUNTAS", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setAlignmentX(CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(12));

        content.add(buildSelectorPanel());
        content.add(Box.createVerticalStrut(12));
        content.add(buildFormPanel());
        content.add(Box.createVerticalStrut(12));
        content.add(buildStatePanel());

        setContentPane(new JScrollPane(content));

        btnLoad.addActionListener(event -> onLoadQuestion());
        btnUpdateState.addActionListener(event -> onUpdateState());
        setFormEnabled(false);
    }

    private JPanel buildSelectorPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Seleccionar pregunta"));
        panel.add(new JLabel("Pregunta:"), BorderLayout.WEST);
        panel.add(cmbQuestions, BorderLayout.CENTER);
        panel.add(btnLoad, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Formulario de pregunta"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtStatement.setLineWrap(true);
        txtStatement.setWrapStyleWord(true);
        txtStatement.setEditable(false);
        txtStatement.setBackground(new Color(0xF5, 0xF5, 0xF5));

        txtOptions.setEditable(false);
        txtOptions.setBackground(new Color(0xF5, 0xF5, 0xF5));

        int row = 0;
        addFormRow(panel, gbc, row++, "Id:", lblId);
        addFormRow(panel, gbc, row++, "Nombre:", lblName);
        addFormRow(panel, gbc, row++, "Pregunta:", new JScrollPane(txtStatement));
        addFormRow(panel, gbc, row++, "Opciones:", new JScrollPane(txtOptions));
        addFormRow(panel, gbc, row++, "Respuesta correcta:", lblCorrect);

        lblCurrentState.setFont(lblCurrentState.getFont().deriveFont(Font.BOLD));
        addFormRow(panel, gbc, row, "Estado actual:", lblCurrentState);

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row,
                            String label, java.awt.Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private JPanel buildStatePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Cambiar estado"));
        panel.add(new JLabel("Nuevo estado:"), BorderLayout.WEST);
        panel.add(cmbNewState, BorderLayout.CENTER);
        panel.add(btnUpdateState, BorderLayout.EAST);
        panel.setPreferredSize(new Dimension(520, 70));
        return panel;
    }

    private void loadQuestionsIntoCombo() {
        cmbQuestions.removeAllItems();
        List<Question> questions = controller.listQuestions();
        for (Question question : questions) {
            cmbQuestions.addItem(question);
        }
    }

    private void onLoadQuestion() {
        Question item = (Question) cmbQuestions.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una pregunta del listado.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Optional<Question> found = controller.loadQuestion(item.getId());
        if (found.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La pregunta ya no existe en el banco.",
                    "No encontrada", JOptionPane.ERROR_MESSAGE);
            return;
        }
        selectedQuestion = found.get();
        showQuestion(selectedQuestion);
        setFormEnabled(true);
    }

    private void showQuestion(Question question) {
        lblId.setText(question.getId());
        lblName.setText(question.getName());
        txtStatement.setText(question.getStatement());

        StringBuilder options = new StringBuilder();
        List<String> values = question.getDistractors().getOptions();
        for (int i = 0; i < values.size(); i++) {
            options.append(QuestionDistractors.letterOf(i))
                   .append(". ")
                   .append(values.get(i))
                   .append(System.lineSeparator());
        }
        txtOptions.setText(options.toString().trim());

        lblCorrect.setText(String.valueOf(question.getCorrectAnswerLetter()));
        lblCurrentState.setText(question.getState().getLabel());
        cmbNewState.setSelectedItem(question.getState());
    }

    private void onUpdateState() {
        if (selectedQuestion == null) {
            JOptionPane.showMessageDialog(this,
                    "Primero cargue una pregunta.",
                    "Sin pregunta", JOptionPane.WARNING_MESSAGE);
            return;
        }
        QuestionState newState = (QuestionState) cmbNewState.getSelectedItem();
        try {
            Question updated = controller.changeState(selectedQuestion.getId(), newState);
            selectedQuestion = updated;
            showQuestion(updated);
            // Las dos vistas observadoras ya fueron refrescadas por el modelo.
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setFormEnabled(boolean enabled) {
        cmbNewState.setEnabled(enabled);
        btnUpdateState.setEnabled(enabled);
    }
}
