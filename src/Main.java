import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Test database connection first
        try {
            System.out.println("🔍 Testing database connection...");
            List<Book> books = DatabaseHelper.getAllBooks();
            System.out.println("✅ Found " + books.size() + " books in database!");

            // Show first few books
            for (int i = 0; i < Math.min(3, books.size()); i++) {
                System.out.println("📚 " + books.get(i).getDisplayInfo());
            }

            // Test authors
            List<Author> authors = DatabaseHelper.getAllAuthors();
            System.out.println("✅ Found " + authors.size() + " authors!");

        } catch (Exception e) {
            System.out.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }

        // Create sample book cover images
        System.out.println("🖼️ Creating sample book cover images...");
        BookCoverDisplayPanel.createSampleImages();

        // Update some books in database to use the sample images
        updateBookCovers();

        // Start the application with default look and feel
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private static void updateBookCovers() {
        try {
            // Update first 5 books to use the 5 sample images (Book1.jpg to Book5.jpg)
            List<Book> books = DatabaseHelper.getAllBooks();
            System.out.println("📚 Assigning sample covers to first 5 books...");

            // Assign sample covers to first 5 books only (as per requirement)
            for (int i = 0; i < Math.min(5, books.size()); i++) {
                Book book = books.get(i);
                String coverFileName = "Book" + (i + 1) + ".jpg";  // Book1.jpg, Book2.jpg, etc.

                // Update the book's cover field in database
                DatabaseHelper.updateBookCover(book.getBookId(), coverFileName);
                System.out.println("📖 Book ID " + book.getBookId() + " (" + book.getTitle() + ") → " + coverFileName);
            }

            System.out.println("✅ Sample covers assigned to first 5 books!");

        } catch (Exception e) {
            System.err.println("Error updating book covers: " + e.getMessage());
        }
    }
}