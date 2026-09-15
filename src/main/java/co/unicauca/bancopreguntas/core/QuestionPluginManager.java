package co.unicauca.bancopreguntas.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class QuestionPluginManager {
    private List<QuestionPlugin> plugins = new ArrayList<>();

    public QuestionPluginManager() {
        loadPlugins();
    }

    private void loadPlugins() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("plugins.properties")) {
            if (input == null) {
                System.err.println("No se encontró el archivo plugins.properties");
                return;
            }
            Properties prop = new Properties();
            prop.load(input);

            for (String key : prop.stringPropertyNames()) {
                String className = prop.getProperty(key);
                Class<?> clazz = Class.forName(className);
                QuestionPlugin plugin = (QuestionPlugin) clazz.getDeclaredConstructor().newInstance();
                plugins.add(plugin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public QuestionPlugin getPluginForType(String type) {
        for (QuestionPlugin plugin : plugins) {
            if (plugin.supports(type)) {
                return plugin;
            }
        }
        return null;
    }
}
