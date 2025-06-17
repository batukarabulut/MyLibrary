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
        initializeComponents();
        setupLayout();
        addEventListeners();
    }

    private void initializeComponents() {
        bookIdField = new JTextField(10);
        displayButton = new JButton("Display Book Cover");
        imageLabel = new JLabel();
        bookInfoLabel = new JLabel("<html><center>Enter Book ID and click Display</center></html>");

        // Set up image label
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        imageLabel.setPreferredSize(new Dimension(300, 400));

        // Set up scroll pane for image
        imageScrollPane = new JScrollPane(imageLabel);
        imageScrollPane.setPreferredSize(new Dimension(320, 420));
        imageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        imageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Style components
        displayButton.setBackground(new Color(70, 130, 180));
        displayButton.setForeground(Color.WHITE);
        displayButton.setFocusPainted(false);
        displayButton.setFont(new Font("Segoe UI", Font.BOLD, 12));

        bookInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookInfoLabel.setHorizontalAlignment(JLabel.CENTER);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("📖 Book Cover Display"));

        // Top panel for input
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Book ID:"));
        inputPanel.add(bookIdField);
        inputPanel.add(displayButton);

        // Center panel for image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageScrollPane, BorderLayout.CENTER);
        imagePanel.add(bookInfoLabel, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.NORTH);
        add(imagePanel, BorderLayout.CENTER);
    }

    private void addEventListeners() {
        displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayBookCover();
            }
        });

        // Allow Enter key to trigger display
        bookIdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayBookCover();
            }
        });
    }

    private void displayBookCover() {
        String bookIdText = bookIdField.getText().trim();

        if (bookIdText.isEmpty()) {
            showError("Please enter a Book ID!");
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdText);

            // Get book information from database
            Book book = DatabaseHelper.getBookInfo(bookId);

            if (book == null) {
                showError("Book with ID " + bookId + " not found!");
                return;
            }

            // Display book information
            String bookInfo = String.format(
                    "<html><center><b>%s</b><br>by %s (%d)<br>%d pages</center></html>",
                    book.getTitle(),
                    book.getAuthorName(),
                    book.getYear(),
                    book.getNumberOfPages()
            );
            bookInfoLabel.setText(bookInfo);

            // Get cover image path from database
            String coverPath = book.getCover();

            if (coverPath == null || coverPath.trim().isEmpty()) {
                // If no cover specified, try default naming: Book1.jpg, Book2.jpg, etc.
                coverPath = "Book" + bookId + ".jpg";
            }

            // Try to load and display the image
            displayImage(coverPath, book.getTitle());

        } catch (NumberFormatException e) {
            showError("Please enter a valid Book ID number!");
        } catch (Exception e) {
            showError("Error loading book: " + e.getMessage());
        }
    }

    private void displayImage(String imagePath, String bookTitle) {
        try {
            // Try to load image from project directory
            File imageFile = new File(imagePath);

            if (!imageFile.exists()) {
                // Try alternative paths and naming conventions
                String[] alternativePaths = {
                        imagePath,
                        "covers/" + imagePath,
                        "images/" + imagePath,
                        imagePath.toLowerCase(),
                        imagePath.toUpperCase()
                };

                boolean found = false;
                for (String altPath : alternativePaths) {
                    File altFile = new File(altPath);
                    if (altFile.exists()) {
                        imageFile = altFile;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    showPlaceholder("Image not found: " + imagePath + "\n\nTried locations:\n" +
                            "• Project root\n• covers/ folder\n• images/ folder");
                    return;
                }
            }

            // Load and scale the image
            BufferedImage originalImage = ImageIO.read(imageFile);

            if (originalImage == null) {
                showPlaceholder("Invalid image file: " + imagePath);
                return;
            }

            // Scale image to fit display area while maintaining aspect ratio
            ImageIcon scaledIcon = scaleImage(originalImage, 280, 380);
            imageLabel.setIcon(scaledIcon);
            imageLabel.setText(""); // Clear any text

            System.out.println("✅ Successfully loaded image: " + imageFile.getAbsolutePath());

        } catch (Exception e) {
            showPlaceholder("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ImageIcon scaleImage(BufferedImage originalImage, int maxWidth, int maxHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Calculate scaling factor to maintain aspect ratio
        double scaleX = (double) maxWidth / originalWidth;
        double scaleY = (double) maxHeight / originalHeight;
        double scale = Math.min(scaleX, scaleY);

        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        // Create scaled image
        Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

        return new ImageIcon(scaledImage);
    }

    private void showPlaceholder(String message) {
        imageLabel.setIcon(null);
        imageLabel.setText("<html><center><div style='padding: 20px;'>" +
                "<h3>📚</h3>" +
                "<p>" + message + "</p>" +
                "<br><p><i>Place image files (Book1.jpg, Book2.jpg, etc.)<br>" +
                "in your project directory</i></p>" +
                "</div></center></html>");
    }

    private void showError(String message) {
        imageLabel.setIcon(null);
        imageLabel.setText("<html><center><div style='padding: 20px; color: red;'>" +
                "<h3>❌</h3>" +
                "<p>" + message + "</p>" +
                "</div></center></html>");
        bookInfoLabel.setText("<html><center>Enter a valid Book ID</center></html>");
    }

    // Method to create sample cover images programmatically for testing
    public static void createSampleImages() {
        try {
            System.out.println("📁 Creating 5 sample book cover images...");

            // Create exactly 5 sample images as per requirement
            Color[] colors = {
                    new Color(70, 130, 180),   // Steel Blue
                    new Color(220, 20, 60),    // Crimson
                    new Color(34, 139, 34),    // Forest Green
                    new Color(255, 140, 0),    // Dark Orange
                    new Color(138, 43, 226)    // Blue Violet
            };

            for (int i = 1; i <= 5; i++) {
                String fileName = "Book" + i + ".jpg";
                File imageFile = new File(fileName);

                if (!imageFile.exists()) {
                    // Create simple book cover image
                    BufferedImage image = new BufferedImage(200, 300, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = image.createGraphics();

                    // Enable antialiasing for better text
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    // Background color
                    g2d.setColor(colors[i-1]);
                    g2d.fillRect(0, 0, 200, 300);

                    // Add white border
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(50, 100, 100, 60);

                    // Add "Book-1" text (as shown in requirement image)
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Arial", Font.BOLD, 16));
                    String bookText = "Book-" + i;
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(bookText);
                    int x = (200 - textWidth) / 2;
                    int y = 135;
                    g2d.drawString(bookText, x, y);

                    // Add simple border around the whole image
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(1, 1, 198, 298);

                    g2d.dispose();

                    // Save image as JPEG
                    ImageIO.write(image, "jpg", imageFile);
                    System.out.println("✅ Created: " + fileName);
                } else {
                    System.out.println("📷 Already exists: " + fileName);
                }
            }

            System.out.println("🎨 Sample image creation completed!");

        } catch (Exception e) {
            System.err.println("❌ Error creating sample images: " + e.getMessage());
            e.printStackTrace();
        }
    }
}