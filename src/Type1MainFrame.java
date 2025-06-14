import javax.swing.*;
import java.awt.*;

public class Type1MainFrame extends JFrame {

    public Type1MainFrame() {
        initializeComponents();

        // Frame settings
        setTitle("MyLibrary - Administrator Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Temporary content - we'll implement this properly later
        JLabel welcomeLabel = new JLabel("Welcome Administrator! (Type 1 User)", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(welcomeLabel, BorderLayout.CENTER);

        // Add a simple menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu functionsMenu = new JMenu("Functions");

        // Add menu items for all 10 functions (Type 1 has access to all)
        functionsMenu.add(new JMenuItem("1. Add Book"));
        functionsMenu.add(new JMenuItem("2. Delete Book"));
        functionsMenu.add(new JMenuItem("3. Display Book Info"));
        functionsMenu.add(new JMenuItem("4. Search Author"));
        functionsMenu.add(new JMenuItem("5. Display-Edit-Update Book"));
        functionsMenu.add(new JMenuItem("6. Display Favorite Books"));
        functionsMenu.add(new JMenuItem("7. Display Favorite Authors"));
        functionsMenu.add(new JMenuItem("8. Display Unread Books"));
        functionsMenu.add(new JMenuItem("9. Check Release Notifications"));
        functionsMenu.add(new JMenuItem("10. Display Book Cover"));

        menuBar.add(functionsMenu);
        setJMenuBar(menuBar);
    }
}