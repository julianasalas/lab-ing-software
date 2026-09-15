package co.unicauca.taller2.ui;

import co.unicauca.taller2.model.User;
import co.unicauca.taller2.service.AuthService;
import co.unicauca.taller2.service.AuthenticationException;
import co.unicauca.taller2.service.UserService;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de inicio de sesión.
 *
 * Esta clase es parte de la capa de presentación (UI): su única
 * responsabilidad es capturar el login/contraseña ingresados por el
 * usuario y delegar la validación de credenciales a {@link AuthService}.
 * No contiene lógica de negocio ni de acceso a datos (SRP).
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;
    private final UserService userService;
    private final RoleMenuProvider roleMenuProvider;

    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);

    public LoginFrame(AuthService authService, UserService userService, RoleMenuProvider roleMenuProvider) {
        super("Gestión de Usuarios - Iniciar sesión");
        this.authService = authService;
        this.userService = userService;
        this.roleMenuProvider = roleMenuProvider;

        buildUI();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Usuario (login):"), gbc);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton loginButton = new JButton("Iniciar sesión");
        loginButton.addActionListener(e -> handleLogin());

        JButton registerButton = new JButton("Registrarse");
        registerButton.addActionListener(e -> openRegisterDialog());

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(loginButton);
        buttonsPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(buttonsPanel, gbc);

        getRootPane().setDefaultButton(loginButton);
        pack();
        setLocationRelativeTo(null);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();

        try {
            User authenticatedUser = authService.login(username, new String(password));
            JOptionPane.showMessageDialog(this,
                    "Bienvenido/a, " + authenticatedUser.getFullName() + " (" + authenticatedUser.getRole().toDisplayName() + ")",
                    "Inicio de sesión exitoso", JOptionPane.INFORMATION_MESSAGE);

            new MainMenuFrame(authenticatedUser, roleMenuProvider).setVisible(true);
            this.dispose();
        } catch (AuthenticationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de autenticación", JOptionPane.ERROR_MESSAGE);
        } finally {
            java.util.Arrays.fill(password, ' ');
        }
    }

    private void openRegisterDialog() {
        RegisterFrame registerFrame = new RegisterFrame(userService);
        registerFrame.setVisible(true);
    }
}
