import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculadora extends JFrame {
    private JTextField displayInput;
    private JTextField displayStored;

    private JPanel numPad;
    private JPanel operationsPad;
    private String currentMode = "Libre";
    private JLabel modeLabel;
    private JLabel nameLabel;

    private double storedValue = 0;
    private String currentOperation = "";
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
        numPad = new JPanel(new GridLayout(4, 3));
        operationsPad = new JPanel(new GridLayout(6, 1));

        displayInput = new JTextField("0");
        displayInput.setHorizontalAlignment(JTextField.RIGHT);
        displayInput.setEditable(false);
        displayStored = new JTextField();
        displayStored.setHorizontalAlignment(JTextField.RIGHT);
        displayStored.setEditable(false);
        displayPanel.add(displayStored);
        displayPanel.add(displayInput);

        for (int i = 1; i <= 9; i++) {
            addButton(numPad, String.valueOf(i));
        }

        addButton(numPad, "0");
        addButton(numPad, ".");
        addButton(numPad, "+/-");

        addButton(operationsPad, "+");
        addButton(operationsPad, "-");
        addButton(operationsPad, "*");
        addButton(operationsPad, "/");
        addButton(operationsPad, "=");
        addButton(operationsPad, "C");

        modeLabel = new JLabel("Modo: " + currentMode);
        nameLabel = new JLabel("Alumno: Nico");

        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.add(modeLabel);
        southPanel.add(nameLabel);

        add(displayPanel, BorderLayout.NORTH);
        add(numPad, BorderLayout.CENTER);
        add(operationsPad, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);

        assignListeners();

        
    }

    private void addButton(JPanel panel, String label) {
        JButton button = new JButton(label);
        panel.add(button);
    }

    private void assignListeners() {
        // Agregar ActionListener a los botones numéricos y "."
        for (Component c : numPad.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.addActionListener(e -> handleNumberInput(button.getText()));
            }
        }

        // Agregar ActionListener a los botones de operación
        for (Component c : operationsPad.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.addActionListener(e -> handleOperation(button.getText()));
            }
        }
    }

    private void handleNumberInput(String input) {
        if (newInput) {
            displayInput.setText(input.equals(".") ? "0." : input);
            newInput = false;
        } else {
            if (input.equals(".") && displayInput.getText().contains(".")) {
                return; // Evitar múltiples puntos decimales
            }
            displayInput.setText(displayInput.getText() + input);
        }
    }

    private void handleOperation(String operation) {
        switch (operation) {
            case "C":
                storedValue = 0;
                displayInput.setText("0");
                displayStored.setText("");
                currentOperation = "";
                newInput = true;
                break;
            case "+/-":
                double currentValue = Double.parseDouble(displayInput.getText());
                displayInput.setText(String.valueOf(-currentValue));
                break;
            case "=":
                if (!currentOperation.isEmpty()) {
                    performCalculation();
                    currentOperation = "";
                    displayStored.setText("");
                }
                break;
            default: // Operaciones básicas
                if (!currentOperation.isEmpty()) {
                    performCalculation();
                } else {
                    storedValue = Double.parseDouble(displayInput.getText());
                }
                currentOperation = operation;
                displayStored.setText(storedValue + " " + currentOperation);
                newInput = true;
                break;
        }
    }

    private void performCalculation() {
        double currentValue = Double.parseDouble(displayInput.getText());
        switch (currentOperation) {
            case "+":
                storedValue += currentValue;
                break;
            case "-":
                storedValue -= currentValue;
                break;
            case "*":
                storedValue *= currentValue;
                break;
            case "/":
                if (currentValue != 0) {
                    storedValue /= currentValue;
                } else {
                    JOptionPane.showMessageDialog(this, "Error: División por cero");
                    return;
                }
                break;
        }
        displayInput.setText(String.valueOf(storedValue));
        newInput = true;
    }


    public static void main(String[] args) {
            Calculadora calc = new Calculadora();
            calc.setVisible(true);
        };
}