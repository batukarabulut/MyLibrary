import javax.swing.*;
import java.awt.*;

public class Type2MainFrame extends JFrame {

    public Type2MainFrame() {
        initializeComponents();

        // Frame settings
        setTitle("MyLibrary - User Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Temporary content
        JLabel welcomeLabel = new JLabel("Welcome User! (Type 2 User)", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(welcomeLabel, BorderLayout.CENTER);

        // Add menu bar with limited functions (3, 4, 6, 7, 8, 10 only)
        JMenuBar menuBar = new JMenuBar();
        JMenu functionsMenu = new JMenu("Functions");

        functionsMenu.add(new JMenuItem("3. Display Book Info"));
        functionsMenu.add(new JMenuItem("4. Search Author"));
        functionsMenu.add(new JMenuItem("6. Display Favorite Books"));
        functionsMenu.add(new JMenuItem("7. Display Favorite Authors"));
        functionsMenu.add(new JMenuItem("8. Display Unread Books"));
        functionsMenu.add(new JMenuItem("10. Display Book Cover"));

        menuBar.add(functionsMenu);
        setJMenuBar(menuBar);
    }
}