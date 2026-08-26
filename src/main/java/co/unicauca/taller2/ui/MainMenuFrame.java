package co.unicauca.taller2.ui;

import co.unicauca.taller2.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Tablero/menú principal que se muestra tras un inicio de sesión exitoso.
 *
 * Las opciones que se listan dependen del rol del usuario autenticado, y
 * se obtienen a través de la abstracción {@link RoleMenuProvider}
 * (Principio de Inversión de Dependencias - DIP): esta ventana no sabe
 * cómo se decide qué opciones mostrar para cada rol, solo pide la lista
 * ya resuelta.
 */
public class MainMenuFrame extends JFrame {

    public MainMenuFrame(User user, RoleMenuProvider roleMenuProvider) {
        super("Tablero principal - " + user.getRole().toDisplayName());
        buildUI(user, roleMenuProvider.getMenuOptions(user.getRole()));
    }

    private void buildUI(User user, List<String> options) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel welcomeLabel = new JLabel(
                "Bienvenido/a, " + user.getFullName() + "  |  Rol: " + user.getRole().toDisplayName(),
                SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(welcomeLabel, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        options.forEach(listModel::addElement);
        JList<String> optionsList = new JList<>(listModel);
        add(new JScrollPane(optionsList), BorderLayout.CENTER);

        setSize(420, 320);
        setLocationRelativeTo(null);
    }
}
