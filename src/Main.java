import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set Look and Feel (optional)
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Use default if Nimbus not available
        }

        // Start the application with LoginFrame
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}