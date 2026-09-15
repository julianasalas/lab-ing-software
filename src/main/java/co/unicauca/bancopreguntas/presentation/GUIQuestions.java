package co.unicauca.bancopreguntas.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import co.unicauca.bancopreguntas.core.Question;
import co.unicauca.bancopreguntas.core.QuestionMicrokernel;
import co.unicauca.bancopreguntas.core.QuestionRequest;

public class GUIQuestions extends JFrame {
    private QuestionMicrokernel microkernel;
    private JTextField txtTitle, txtCategory, txtCorrectAnswer;
    private JTextArea txtContent, txtOptions;
    private JComboBox<String> cbType;
    private JTextArea txtOutput;

    public GUIQuestions() {
        microkernel = new QuestionMicrokernel();
        initUI();
    }

    private void initUI() {
        setTitle("Banco de Preguntas Saber PRO - Microkernel & Pipeline");
        setSize(650, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Título de la Pregunta:"));
        txtTitle = new JTextField();
        formPanel.add(txtTitle);

        formPanel.add(new JLabel("Tipo de Pregunta / Plugin:"));
        cbType = new JComboBox<>(new String[]{"MULTIPLE_CHOICE", "CASE_STUDY", "PIPELINE_VALIDATED"});
        formPanel.add(cbType);

        formPanel.add(new JLabel("Opciones (separadas por coma):"));
        txtOptions = new JTextArea(2, 20);
        formPanel.add(new JScrollPane(txtOptions));

        formPanel.add(new JLabel("Respuesta Correcta:"));
        txtCorrectAnswer = new JTextField();
        formPanel.add(txtCorrectAnswer);

        formPanel.add(new JLabel("Categoría / Competencia:"));
        txtCategory = new JTextField();
        formPanel.add(txtCategory);

        JButton btnCreate = new JButton("Procesar y Crear Pregunta");
        btnCreate.setBackground(new Color(30, 144, 255));
        btnCreate.setForeground(Color.WHITE);
        formPanel.add(btnCreate);

        add(formPanel, BorderLayout.NORTH);

        txtContent = new JTextArea(4, 20);
        txtContent.setBorder(BorderFactory.createTitledBorder("Contenido / Enunciado de la Pregunta"));
        add(new JScrollPane(txtContent), BorderLayout.CENTER);

        txtOutput = new JTextArea(8, 20);
        txtOutput.setEditable(false);
        txtOutput.setBorder(BorderFactory.createTitledBorder("Preguntas Almacenadas en el Microkernel"));
        add(new JScrollPane(txtOutput), BorderLayout.SOUTH);

        btnCreate.addActionListener(e -> onCreateQuestion());
    }

    private void onCreateQuestion() {
        try {
            String title = txtTitle.getText();
            String type = (String) cbType.getSelectedItem();
            String content = txtContent.getText();
            List<String> options = Arrays.stream(txtOptions.getText().split(","))
                                         .map(String::trim)
                                         .filter(s -> !s.isEmpty())
                                         .toList();
            String correctAnswer = txtCorrectAnswer.getText();
            String category = txtCategory.getText();

            QuestionRequest req = new QuestionRequest(title, content, type, options, correctAnswer, category, "MEDIA");
            Question q = microkernel.processAndAddQuestion(req);

            JOptionPane.showMessageDialog(this, "¡Pregunta procesada y agregada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            updateOutput();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

   private void updateOutput() {
        StringBuilder sb = new StringBuilder();
        for (Question q : microkernel.getQuestions().values()) {
            sb.append("ID: ").append(q.getId())
              .append(" | Título: ").append(q.getTitle())
              .append(" | Tipo: ").append(q.getType())
              .append(" | Categoría: ").append(q.getCategory())
              .append("\n");
        }
        txtOutput.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUIQuestions().setVisible(true));
    }
}
