import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculadora extends JFrame {
    private JPanel naumPad;
    private JPanel operationsPad;
    private JLabel modeLabel;
    private JLabel nameLabel;
    

    public Calculadora() {

        setTitle("Calculadora");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width / 4;
        int height = 600;
        setSize(width, height);
        setLocationRelativeTo(null);

    }

    public static void main(String[] args) {
            Calculadora calc = new Calculadora();
            calc.setVisible(true);
        };
}