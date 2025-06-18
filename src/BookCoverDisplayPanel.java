import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class BookCoverDisplayPanel extends JPanel {
    private JTextField bookIdField;
    private JButton displayButton;
    private JLabel imageLabel;
    private JLabel bookInfoLabel;
    private JScrollPane imageScrollPane;

    public BookCoverDisplayPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Book Cover Display"));

        createComponents();
        layoutComponents();
        addListeners();
    }

    private void createComponents() {
        // Text field
        bookIdField = new JTextField(15);

        // Button - GUARANTEED BLUE
        displayButton = new JButton("Display Book Cover") {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(41, 128, 185)); // Blue
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.WHITE);
                FontMetrics fm = g.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g.drawString(text, x, y);
            }
        };
        displayButton.setPreferredSize(new Dimension(150, 30));
        displayButton.setFocusPainted(false);
        displayButton.setBorderPainted(false);

        // Image display
        imageLabel = new JLabel("Enter Book ID and click Display", JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        imageLabel.setPreferredSize(new Dimension(300, 400));

        imageScrollPane = new JScrollPane(imageLabel);
        imageScrollPane.setPreferredSize(new Dimension(320, 420));

        bookInfoLabel = new JLabel("Enter Book ID and click Display", JLabel.CENTER);
    }

    private void layoutComponents() {
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Book ID: "));
        topPanel.add(bookIdField);
        topPanel.add(displayButton);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(imageScrollPane, BorderLayout.CENTER);
        centerPanel.add(bookInfoLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void addListeners() {
        displayButton.addActionListener(e -> displayBookCover());
        bookIdField.addActionListener(e -> displayBookCover());
    }

    private void displayBookCover() {
        String bookIdText = bookIdField.getText().trim();

        if (bookIdText.isEmpty()) {
            showMessage("Please enter a Book ID!", Color.RED);
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdText);
            Book book = DatabaseHelper.getBookInfo(bookId);

            if (book == null) {
                showMessage("Book with ID " + bookId + " not found!", Color.RED);
                return;
            }

            // Show book info
            bookInfoLabel.setText(String.format(
                    "<html><center><b>%s</b><br>by %s (%d)<br>%d pages</center></html>",
                    book.getTitle(), book.getAuthorName(), book.getYear(), book.getNumberOfPages()
            ));

            // Load image
            String imagePath = book.getCover();
            if (imagePath == null || imagePath.trim().isEmpty()) {
                imagePath = "Book" + bookId + ".jpg";
            }

            loadImage(imagePath);

        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number!", Color.RED);
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), Color.RED);
        }
    }

    private void loadImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);

            if (!imageFile.exists()) {
                // Try alternative paths
                String[] paths = {
                        imagePath,
                        "covers/" + imagePath,
                        "images/" + imagePath
                };

                boolean found = false;
                for (String path : paths) {
                    File file = new File(path);
                    if (file.exists()) {
                        imageFile = file;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    showMessage("Image not found: " + imagePath, Color.ORANGE);
                    return;
                }
            }

            BufferedImage image = ImageIO.read(imageFile);
            if (image != null) {
                ImageIcon icon = new ImageIcon(image.getScaledInstance(280, 380, Image.SCALE_SMOOTH));
                imageLabel.setIcon(icon);
                imageLabel.setText("");
            } else {
                showMessage("Invalid image file", Color.RED);
            }

        } catch (Exception e) {
            showMessage("Error loading image: " + e.getMessage(), Color.RED);
        }
    }

    private void showMessage(String message, Color color) {
        imageLabel.setIcon(null);
        imageLabel.setText("<html><center><font color='" +
                (color == Color.RED ? "red" : color == Color.ORANGE ? "orange" : "black") +
                "'>" + message + "</font></center></html>");
    }

    public static void createSampleImages() {
        try {
            Color[] colors = {
                    new Color(70, 130, 180),
                    new Color(220, 20, 60),
                    new Color(34, 139, 34),
                    new Color(255, 140, 0),
                    new Color(138, 43, 226)
            };

            for (int i = 1; i <= 5; i++) {
                String fileName = "Book" + i + ".jpg";
                File imageFile = new File(fileName);

                if (!imageFile.exists()) {
                    BufferedImage image = new BufferedImage(200, 300, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = image.createGraphics();

                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Background
                    g2d.setColor(colors[i-1]);
                    g2d.fillRect(0, 0, 200, 300);

                    // White rectangle
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(50, 100, 100, 60);

                    // Text
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Arial", Font.BOLD, 16));
                    String text = "Book-" + i;
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (200 - fm.stringWidth(text)) / 2;
                    g2d.drawString(text, x, 135);

                    // Border
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(1, 1, 198, 298);

                    g2d.dispose();
                    ImageIO.write(image, "jpg", imageFile);
                    System.out.println("Created: " + fileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}