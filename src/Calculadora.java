import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class Calculadora extends JFrame {
    private JTextField displayTexto;
    private JTextField displayAlmacenado;
    private JPanel padNumerico;
    private JPanel padOperaciones;
    private String currentMode = "Ratón";
    private JLabel etiquetaModo;
    private JLabel etiquetaNombre;
    private double valorAlmacenado = 0;
    private String operacionActual = "";
    private boolean newInput = true;
    private boolean tecladoActivo = true; 
    private boolean ratonActivo = true;
    private final HashMap<String, JButton> keyButtonMap = new HashMap<>(); // Mapear teclas a botones


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
        addButton(padOperaciones, "=");; // Color rojo para números negativos
        addButton(padOperaciones, "C");

        // Crear un panel para los botones de cambio de modo
        JPanel modePanel = new JPanel(new FlowLayout());
        JButton btnRaton = new JButton("Ratón");
        JButton btnTeclado = new JButton("Teclado");
        JButton btnLibre = new JButton("Libre");

        // Añadir los botones al panel
        modePanel.add(btnRaton);
        modePanel.add(btnTeclado);
        modePanel.add(btnLibre);

        // Añadir el panel al contenedor principal (en el lugar correspondiente)
        add(modePanel, BorderLayout.SOUTH);

        // Añadir acciones a los botones para cambiar el modo
        btnRaton.addActionListener(e -> setMode("Ratón"));
        btnTeclado.addActionListener(e -> setMode("Teclado numérico"));
        btnLibre.addActionListener(e -> setMode("Libre"));

        etiquetaModo = new JLabel("Modo: " + currentMode);
        etiquetaNombre = new JLabel("Alumno: Nico");

        JPanel southPanel = new JPanel(new GridLayout(2, 1));
       // southPanel.add(etiquetaModo);
       // southPanel.add(etiquetaNombre);

        add(displayPanel, BorderLayout.NORTH);
        add(padNumerico, BorderLayout.CENTER);
        add(padOperaciones, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
        add(modePanel, BorderLayout.SOUTH);

        asignarListeners();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                teclasPadNumerico(e);
            }
        });
        
        setFocusable(true); // Asegurar que el JFrame capture los eventos del teclado
        requestFocusInWindow();
        
    }

    private void addButton(JPanel panel, String label) {
    JButton button = new JButton(label) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void updateUI() {
                super.updateUI();
                setContentAreaFilled(false);
            }
        };

        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 26));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setPreferredSize(new Dimension(150, 50));

        panel.add(button);

        // Mapear teclas con botones
        keyButtonMap.put(label, button);
    } 

    private void asignarListeners() {
        for (Component c : padNumerico.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.addActionListener(e -> {
                    if (ratonActivo) {  // Si el modo de ratón está activo
                        manejarInput(button.getText());  // Maneja la entrada con el ratón
                        establecerEnfoque();  // Restablece el enfoque
                    }
                });
            }
        }
    
        for (Component c : padOperaciones.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.addActionListener(e -> {
                    if (ratonActivo) {  // Si el modo de ratón está activo
                        manejoOperaciones(button.getText());  // Maneja las operaciones con el ratón
                        establecerEnfoque();  // Restablece el enfoque
                    }
                });
            }
        }
    
        // Usamos keyTyped en lugar de keyPressed para evitar la duplicación de entradas
        if (tecladoActivo) {
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    teclasPadNumerico(e);  // Maneja las teclas numéricas
                }
            });
        }
    
        // Asegurarse de que el JFrame pueda captar los eventos de teclado
        setFocusable(true);
        requestFocusInWindow();
    }

    private void manejarInput(String input) {
        if (newInput) {
            displayTexto.setText(input.equals(".") ? "0." : input);
            newInput = false;
        } else {
            if (input.equals(".") && displayTexto.getText().contains(".")) {
                return;
            }
            displayTexto.setText(displayTexto.getText() + input);
        }
        actualizarDisplay();
        establecerEnfoque(); // Restablecer el enfoque
    }
    
    private void teclasPadNumerico(KeyEvent e) {
        if (!tecladoActivo) {
            return;
        }
        // Obtener el código de la tecla
        int keyCode = e.getKeyCode();
        String keyString = "";
        // Mapear las teclas del teclado numérico y del teclado principal
        switch (keyCode) {
            case KeyEvent.VK_NUMPAD0: case KeyEvent.VK_0: keyString = "0"; break;
            case KeyEvent.VK_NUMPAD1: case KeyEvent.VK_1: keyString = "1"; break;
            case KeyEvent.VK_NUMPAD2: case KeyEvent.VK_2: keyString = "2"; break;
            case KeyEvent.VK_NUMPAD3: case KeyEvent.VK_3: keyString = "3"; break;
            case KeyEvent.VK_NUMPAD4: case KeyEvent.VK_4: keyString = "4"; break;
            case KeyEvent.VK_NUMPAD5: case KeyEvent.VK_5: keyString = "5"; break;
            case KeyEvent.VK_NUMPAD6: case KeyEvent.VK_6: keyString = "6"; break;
            case KeyEvent.VK_NUMPAD7: case KeyEvent.VK_7: keyString = "7"; break;
            case KeyEvent.VK_NUMPAD8: case KeyEvent.VK_8: keyString = "8"; break;
            case KeyEvent.VK_NUMPAD9: case KeyEvent.VK_9: keyString = "9"; break;
            case KeyEvent.VK_ADD: case KeyEvent.VK_PLUS: keyString = "+"; break;
            case KeyEvent.VK_SUBTRACT: case KeyEvent.VK_MINUS: keyString = "-"; break;
            case KeyEvent.VK_MULTIPLY: case KeyEvent.VK_ASTERISK: keyString = "*"; break;
            case KeyEvent.VK_DIVIDE: case KeyEvent.VK_SLASH: keyString = "/"; break;
            case KeyEvent.VK_ENTER: keyString = "="; break; // Para Enter
            case KeyEvent.VK_DECIMAL: keyString = "."; break;
            case KeyEvent.VK_BACK_SPACE: keyString = "C"; break; // Para borrar
        }
    
        // Si el keyString es válido, ejecuta el clic del botón correspondiente
        if (keyButtonMap.containsKey(keyString)) {
            keyButtonMap.get(keyString).doClick();  // Ejecuta el clic en el botón correspondiente
        }

    }

    private void manejoOperaciones(String operation) {
        switch (operation) {
            case "C":
                limpiar();
                break;
            case "=":
                if (!operacionActual.isEmpty()) {
                    calcular();
                    operacionActual = "";
                    displayAlmacenado.setText("");
                }
                break;
            default:
                if (!operacionActual.isEmpty()) {
                    calcular();
                } else {
                    valorAlmacenado = Double.parseDouble(normalizarDecimales(displayTexto.getText()));
                }
                operacionActual = operation;
                displayAlmacenado.setText(valorAlmacenado + " " + operacionActual);
                newInput = true;
                break;
        }
            establecerEnfoque(); // Restablecer el enfoque
    }

    private String normalizarDecimales(String text) {
        return text.replace(",", "."); // Reemplaza coma con punto
    }


    private void calcular() {
        double currentValue = Double.parseDouble(normalizarDecimales(displayTexto.getText())); // Normalizar el número ingresado
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
                    valorAlmacenado /= currentValue; // División segura
                } else {
                    JOptionPane.showMessageDialog(this, "Error: División por cero");
                    limpiar();
                    return;
                }
                break;
        }
        displayTexto.setText(String.valueOf(valorAlmacenado)); // Actualizar el texto del resultado
        actualizarDisplay(); // Mostrar en formato con coma
        newInput = true;
    }

    private void setMode(String mode) {
        currentMode = mode;
        etiquetaModo.setText("Modo: " + currentMode);
        switch (mode) {
            case "Ratón":
                tecladoActivo = false;
                ratonActivo = true;
                break;
            case "Teclado numérico":
                tecladoActivo = true;
                ratonActivo = false;
                break;
            case "Libre":
                tecladoActivo = true;
                ratonActivo = true;
                break;
        }
        actualizarListeners();
        establecerEnfoque();
    }

    private void actualizarListeners() {
        for (KeyListener kl : getKeyListeners()) {
            removeKeyListener(kl);
        }
        if (tecladoActivo) {
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    teclasPadNumerico(e);
                }
            });
        }
        for (Component c : padNumerico.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                for (ActionListener al : button.getActionListeners()) {
                    button.removeActionListener(al);
                }
                if (ratonActivo) {
                    button.addActionListener(e -> manejarInput(button.getText()));
                }
            }
        }
        for (Component c : padOperaciones.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                for (ActionListener al : button.getActionListeners()) {
                    button.removeActionListener(al);
                }
                if (ratonActivo) {
                    button.addActionListener(e -> manejoOperaciones(button.getText()));
                }
            }
        }
    }

    private void limpiar() {
        displayTexto.setText("0");
        displayAlmacenado.setText("");
        valorAlmacenado = 0;
        operacionActual = "";
        newInput = true;
    }

    private void establecerEnfoque() {
        this.requestFocusInWindow();
    }

    private void actualizarDisplay() {
        String text = displayTexto.getText();
        if (text.contains("-")) {
            displayTexto.setForeground(Color.RED); // Color rojo para números negativos
        } else {
            displayTexto.setForeground(Color.BLACK); // Color negro para números positivos o cero
        }
        // Asegurar que los decimales se muestren con coma en lugar de punto
        if (text.contains(".")) {
            text = text.replace(".", ",");
            displayTexto.setText(text);
        }
    }
    
    public static void main(String[] args) {
            Calculadora calc = new Calculadora();
            calc.setVisible(true);
        };
}