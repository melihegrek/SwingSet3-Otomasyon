package org.example;

import spark.Spark;
import java.awt.*;
import java.lang.instrument.Instrumentation;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Agent {
    public static final Map<String, Component> components = new HashMap<>();
    public static final List<Component> orderedComponents = new ArrayList<>();
    static JFrame frame = null;

    public static void agentmain(String agentArgs, Instrumentation inst) { // premain -> agentmain
        System.out.println("Agent dynamically loaded.");

        Spark.port(8081);

        Spark.get("/trigger", (req, res) -> {
            String componentPath = req.queryParams("path");
            triggerComponent(componentPath);
            return "Component with path '" + componentPath + "' triggered.";
        });

        Spark.get("/getText", (req, res) -> {
            String componentPath = req.queryParams("path");
            try {
                String text = getText(componentPath);
                if (text != null) {
                    return "Text in component with path '" + componentPath + "': " + text;
                } else {
                    return "Component with path '" + componentPath + "' not found.";
                }
            } catch (Exception e) {
                return "Error getting text: " + e.getMessage();
            }
        });

        new Thread(() -> {
            try {
                Thread.sleep(1000); // GUI yüklenmesini bekle

                // JFrame'i bul
                Window[] windows = Window.getWindows();
                for (Window window : windows) {
                    if (window instanceof JFrame) {
                        frame = (JFrame) window;
                        break;
                    }
                }

                // Bileşenleri bul ve sakla
                if (frame != null) {
                    SwingUtilities.invokeAndWait(() -> findAndStoreComponents(frame, ""));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // TextField ve JLabel metnini alma metodu (path ile)
    private static String getText(String componentPath) {
        Component component = components.get(componentPath);
        if (component instanceof JTextField) {
            return ((JTextField) component).getText();
        } else if (component instanceof JLabel) {
            return ((JLabel) component).getText();
        } else {
            return null; // Diğer bileşen türleri için null döndürün
        }
    }

    private static void findAndStoreComponents(Container container, String currentPath) {
        Map<Class<? extends Component>, Integer> typeCounters = new HashMap<>();

        for (int i = 0; i < container.getComponentCount(); i++) {
            Component component = container.getComponent(i);
            Class<? extends Component> componentClass = component.getClass();
            typeCounters.putIfAbsent(componentClass, 0);
            int componentIndex = typeCounters.get(componentClass);
            typeCounters.put(componentClass, componentIndex + 1);

            String componentPath = currentPath + "/" + componentClass.getSimpleName() + "[" + componentIndex + "]";
            orderedComponents.add(component);
            components.put(componentPath, component);

            String componentText = getComponentText(component);
            System.out.println("Stored component with path: '" + componentPath + "'" +
                    (componentText != null ? " and text: '" + componentText + "'" : ""));

            if (component instanceof Container) {
                findAndStoreComponents((Container) component, componentPath);
            }
        }
    }

    private static String getComponentText(Component component) {
        if (component instanceof AbstractButton) {
            return ((AbstractButton) component).getText();
        } else if (component instanceof JTextField) {
            return ((JTextField) component).getText();
        } else if (component instanceof JLabel) {
            return ((JLabel) component).getText();
        }
        return null;
    }

    private static void triggerComponent(String componentPath) {
        SwingUtilities.invokeLater(() -> {
            Component component = components.get(componentPath);
            if (component != null) {
                if (component instanceof AbstractButton) {
                    ((AbstractButton) component).doClick();
                } else if (component instanceof JTextField) {
                    ((JTextField) component).setText("Triggered!");
                } else {
                    // Diğer bileşen türleri için işlemler ekleyebilirsiniz
                }
                System.out.println("Component with path '" + componentPath + "' triggered.");
            } else {
                System.out.println("Component with path '" + componentPath + "' not found.");
            }
        });
    }

    private static void setComponentBorders(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JComponent) {
                ((JComponent) component).setBorder(new LineBorder(Color.RED));
            }
            if (component instanceof Container) {
                setComponentBorders((Container) component);
            }
        }
    }
}
