package co.unicauca.taller2.ui;

import co.unicauca.taller2.model.Role;
import co.unicauca.taller2.model.UserStatus;
import co.unicauca.taller2.service.UserService;
import co.unicauca.taller2.validation.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Ventana para el registro de un nuevo usuario del sistema.
 *
 * Al igual que {@link LoginFrame}, esta clase solo se encarga de recoger
 * los datos ingresados por el usuario y delegar toda la validación y el
 * cifrado de la contraseña a {@link UserService} (SRP): la interfaz
 * gráfica no valida reglas de negocio por sí misma.
 */
public class RegisterFrame extends JFrame {

    private final UserService userService;

    private final JTextField usernameField = new JTextField(18);
    private final JTextField fullNameField = new JTextField(18);
    private final JComboBox<Role> roleComboBox = new JComboBox<>(Role.values());
    private final JComboBox<UserStatus> statusComboBox = new JComboBox<>(UserStatus.values());
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JPasswordField confirmPasswordField = new JPasswordField(18);

    public RegisterFrame(UserService userService) {
        super("Registro de usuario");
        this.userService = userService;
        buildUI();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        row = addRow(gbc, row, "Usuario (login):", usernameField);
        row = addRow(gbc, row, "Nombre completo:", fullNameField);
        row = addRow(gbc, row, "Rol:", roleComboBox);
        row = addRow(gbc, row, "Estado:", statusComboBox);
        row = addRow(gbc, row, "Contraseña:", passwordField);
        row = addRow(gbc, row, "Confirmar contraseña:", confirmPasswordField);

        JButton registerButton = new JButton("Registrar");
        registerButton.addActionListener(e -> handleRegister());

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        add(registerButton, gbc);

        getRootPane().setDefaultButton(registerButton);
        pack();
        setLocationRelativeTo(null);
    }

    private int addRow(GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        add(field, gbc);
        return row + 1;
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        Role role = (Role) roleComboBox.getSelectedItem();
        UserStatus status = (UserStatus) statusComboBox.getSelectedItem();
        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();

        try {
            if (!Arrays.equals(password, confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            userService.registerUser(username, fullName, role, status, new String(password));

            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos inválidos", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, ' ');
            Arrays.fill(confirmPassword, ' ');
        }
    }
}
