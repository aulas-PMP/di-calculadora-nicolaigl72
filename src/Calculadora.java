import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class Calculadora extends JFrame {
    private JTextField displayTexto;
    private JTextField displayAlmacenado;
    private JPanel padNumerico;
    private JPanel padOperaciones;
    private double valorAlmacenado = 0;
    private String operacionActual = "";
    private boolean newInput = true;
    private boolean tecladoActivo = false; 
    private boolean ratonActivo = true;
    private final HashMap<String, JButton> keyButtonMap = new HashMap<>();

    public Calculadora() {
        setTitle("Calculadora - Nicolás Iglesias Solla");
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
        
        displayAlmacenado = new JTextField();
        displayAlmacenado.setHorizontalAlignment(JTextField.RIGHT);
        displayAlmacenado.setEditable(false);
        displayAlmacenado.setBackground(new Color(132, 227, 155));
        displayAlmacenado.setFont(new Font("Arial", Font.PLAIN, 18)); 
        
        displayTexto = new JTextField("0");
        displayTexto.setHorizontalAlignment(JTextField.RIGHT);
        displayTexto.setEditable(false);
        displayTexto.setBackground(new Color(171, 255, 192));
        displayTexto.setFont(new Font("Arial", Font.BOLD, 24)); 
        
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

        JPanel modePanel = new JPanel(new FlowLayout());
        JButton btnRaton = new JButton("Ratón");
        JButton btnTeclado = new JButton("Teclado");
        JButton btnLibre = new JButton("Libre");

        modePanel.add(btnRaton);
        modePanel.add(btnTeclado);
        modePanel.add(btnLibre);

        btnRaton.addActionListener(e -> setMode("Ratón"));
        btnTeclado.addActionListener(e -> setMode("Teclado numérico"));
        btnLibre.addActionListener(e -> setMode("Libre"));

        JPanel southPanel = new JPanel(new GridLayout(2, 1));


        add(displayPanel, BorderLayout.NORTH);
        add(padNumerico, BorderLayout.CENTER);
        add(padOperaciones, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
        add(modePanel, BorderLayout.SOUTH);

        asignarListeners();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (tecladoActivo) {
                    teclasPadNumerico(e);
                }
            }
        });
        setMode("Libre");
        setFocusable(true);
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
        keyButtonMap.put(label, button);
    }

    private void asignarListeners() {
        for (Component c : padNumerico.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.addActionListener(e -> {
                    if (ratonActivo) {
                        manejarInput(button.getText());
                        establecerEnfoque();
                    }
                });
            }
        }

        for (Component c : padOperaciones.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.addActionListener(e -> {
                    if (ratonActivo) {
                        manejoOperaciones(button.getText());
                        establecerEnfoque();
                    }
                });
            }
        }
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
        actualizarColorTexto();
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
                }
                break;
            default:
                if (!operacionActual.isEmpty()) {
                    calcular();
                } else {
                    valorAlmacenado = Double.parseDouble(displayTexto.getText());
                }
                operacionActual = operation;
                displayAlmacenado.setText(valorAlmacenado + " " + operacionActual);
                newInput = true;
                break;
        }
    }

    private void calcular() {
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
                limpiar();
                return;
            }
            break;
    }
        displayTexto.setText(String.valueOf(valorAlmacenado));
        newInput = true;
        actualizarColorTexto();
    }

    private void setMode(String mode) {
        tecladoActivo = mode.equals("Teclado numérico") || mode.equals("Libre");
        ratonActivo = mode.equals("Ratón") || mode.equals("Libre");

        if (tecladoActivo) {
            requestFocusInWindow(); 
        }
    }

    private void teclasPadNumerico(KeyEvent e) {
        String keyString = "";
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_NUMPAD0:
            case KeyEvent.VK_NUMPAD1:
            case KeyEvent.VK_NUMPAD2:
            case KeyEvent.VK_NUMPAD3:
            case KeyEvent.VK_NUMPAD4:
            case KeyEvent.VK_NUMPAD5:
            case KeyEvent.VK_NUMPAD6:
            case KeyEvent.VK_NUMPAD7:
            case KeyEvent.VK_NUMPAD8:
            case KeyEvent.VK_NUMPAD9:
                keyString = String.valueOf(e.getKeyChar());
                break;
            case KeyEvent.VK_ADD:
                keyString = "+";
                break;
            case KeyEvent.VK_SUBTRACT:
                keyString = "-";
                break;
            case KeyEvent.VK_MULTIPLY:
                keyString = "*";
                break;
            case KeyEvent.VK_DIVIDE:
                keyString = "/";
                break;
            case KeyEvent.VK_ENTER:
                keyString = "=";
                break;
            case KeyEvent.VK_DECIMAL:
                keyString = ".";
                break;
        }

        if (!keyString.isEmpty()) {
            if ("+-*/=C".contains(keyString)) {
                manejoOperaciones(keyString);
            } else {
                manejarInput(keyString);
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

    private void actualizarDisplay() {
        if (displayTexto.getText().length() > 12) {
            displayTexto.setFont(new Font("Arial", Font.PLAIN, 18));
        } else {
            displayTexto.setFont(new Font("Arial", Font.BOLD, 24));
        }
    }
    
    private void actualizarColorTexto() {
        if (Double.parseDouble(displayTexto.getText()) < 0) {
            displayTexto.setForeground(Color.RED);
        } else {
            displayTexto.setForeground(Color.BLACK);
        }
    }

    private void establecerEnfoque() {
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    public static void main(String[] args) {
       Calculadora calc = new Calculadora();
       calc.setVisible(true);
    }
}