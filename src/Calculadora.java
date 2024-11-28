import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.*;

public class Calculadora extends JFrame {
    private JTextField displayTexto;
    private JTextField displayAlmacenado;

    private JPanel padNumerico;
    private JPanel padOperaciones;
    private String currentMode = "Libre";
    private JLabel etiquetaModo;
    private JLabel etiquetaNombre;

    private double valorAlmacenado = 0;
    private String operacionActual = "";
    private boolean newInput = true;
    

    public Calculadora() {

        setTitle("Calculadora");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width / 2;
        int height = 600;
        setSize(width, height);
        setLocationRelativeTo(null);

        JPanel displayPanel = new JPanel(new GridLayout(2, 1));
        padNumerico = new JPanel(new GridLayout(4, 3));
        padOperaciones = new JPanel(new GridLayout(6, 1));
        displayTexto = new JTextField("0");
        displayTexto.setHorizontalAlignment(JTextField.RIGHT);
        displayTexto.setEditable(false);
        displayTexto.setBackground(new Color(171, 255, 192));
        displayTexto.setFont(new Font("Arial", Font.BOLD, 24)); // Fuente más grande
        displayTexto.setPreferredSize(new Dimension(width, 40)); // Más ancho
        displayAlmacenado = new JTextField();
        displayAlmacenado.setHorizontalAlignment(JTextField.RIGHT);
        displayAlmacenado.setEditable(false);
        displayAlmacenado.setBackground(new Color(132, 227, 155));
        displayAlmacenado.setFont(new Font("Arial", Font.BOLD, 24)); // Fuente más grande
        displayAlmacenado.setPreferredSize(new Dimension(width, 40)); // Más ancho
        displayPanel.add(displayAlmacenado);
        displayPanel.add(displayTexto);

        for (int i = 1; i <= 9; i++) {
            addButton(padNumerico, String.valueOf(i));
        }

        addButton(padNumerico, "0");
        addButton(padNumerico, ".");
        addButton(padOperaciones, "+");
        addButton(padOperaciones, "-");
        addButton(padOperaciones, "*");
        addButton(padOperaciones, "/");
        addButton(padOperaciones, "=");
        addButton(padOperaciones, "C");

        etiquetaModo = new JLabel("Modo: " + currentMode);
        etiquetaNombre = new JLabel("Alumno: Nico");

        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.add(etiquetaModo);
        southPanel.add(etiquetaNombre);

        add(displayPanel, BorderLayout.NORTH);
        add(padNumerico, BorderLayout.CENTER);
        add(padOperaciones, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);

        assignListeners();

        
    }

    private void addButton(JPanel panel, String label) {
        JButton button = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dibujar fondo redondeado
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Dibujar el borde negro redondeado
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2)); // Grosor del borde
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setContentAreaFilled(false); // Quitar fondo estándar
            }
        };

        // Configuración del botón
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setFont(new Font("Arial", Font.BOLD, 26));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder()); // Elimina el borde predeterminado
            button.setPreferredSize(new Dimension(150, 50)); // Tamaño del botón

            panel.add(button);
    
        }
        
    

    private void assignListeners() {
        // Agregar ActionListener a los botones numéricos y "."
        for (Component c : padNumerico.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.addActionListener(e -> handleNumberInput(button.getText()));
            }
        }

        // Agregar ActionListener a los botones de operación
        for (Component c : padOperaciones.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.addActionListener(e -> handleOperation(button.getText()));
            }
        }
    }

    private void handleNumberInput(String input) {
        if (newInput) {
            displayTexto.setText(input.equals(".") ? "0." : input);
            newInput = false;
        } else {
            if (input.equals(".") && displayTexto.getText().contains(".")) {
                return; // Evitar múltiples puntos decimales
            }
            displayTexto.setText(displayTexto.getText() + input);
        }
    }

    private void handleOperation(String operation) {
        switch (operation) {
            case "C":
                valorAlmacenado = 0;
                displayTexto.setText("0");
                displayAlmacenado.setText("");
                operacionActual = "";
                newInput = true;
                break;
            case "+/-":
                double currentValue = Double.parseDouble(displayTexto.getText());
                displayTexto.setText(String.valueOf(-currentValue));
                break;
            case "=":
                if (!operacionActual.isEmpty()) {
                    performCalculation();
                    operacionActual = "";
                    displayAlmacenado.setText("");
                }
                break;
            default: // Operaciones básicas
                if (!operacionActual.isEmpty()) {
                    performCalculation();
                } else {
                    valorAlmacenado = Double.parseDouble(displayTexto.getText());
                }
                operacionActual = operation;
                displayAlmacenado.setText(valorAlmacenado + " " + operacionActual);
                newInput = true;
                break;
        }
    }

    private void performCalculation() {
        double currentValue = Double.parseDouble(displayTexto.getText());
        switch (operacionActual) {
            case "+":
                valorAlmacenado += currentValue;
                break;
            case "-":
                valorAlmacenado -= currentValue;
                break;
            case "*":
                valorAlmacenado *= currentValue;
                break;
            case "/":
                if (currentValue != 0) {
                    valorAlmacenado /= currentValue;
                } else {
                    JOptionPane.showMessageDialog(this, "Error: División por cero");
                    return;
                }
                break;
        }
        displayTexto.setText(String.valueOf(valorAlmacenado));
        newInput = true;
    }


    public static void main(String[] args) {
            Calculadora calc = new Calculadora();
            calc.setVisible(true);
        };
}