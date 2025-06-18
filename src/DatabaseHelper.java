import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseHelper {
    private static final String CONFIG_FILE = "config.properties";
    private static String DB_URL;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;

    private static int currentUserId = -1;

    static {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(CONFIG_FILE));
            DB_URL = props.getProperty("db.url");
            DB_USERNAME = props.getProperty("db.user");
            DB_PASSWORD = props.getProperty("db.password");
        } catch (IOException e) {
            System.err.println("⚠️ config.properties dosyası okunamadı: " + e.getMessage());
        }
    }

    public static void setCurrentUser(int userId) {
        currentUserId = userId;
        System.out.println("✅ Current user set to: " + userId);
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
    /**
     * Get all books with current user's personal data
     */
    public static List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        if (currentUserId == -1) {
            System.err.println("❌ No user logged in!");
            return books;
        }

        try (Connection conn = getConnection()) {
            String query = "SELECT b.bookId, b.authorId, b.title, b.year, b.numberOfPages, b.cover, b.about, " +
                    "CONCAT(a.name, ' ', a.surname) as authorName, " +
                    "COALESCE(ub.readStatus, 0) as readStatus, " +
                    "COALESCE(ub.rating, 0) as rating, " +
                    "ub.comments, " +
                    "ub.releaseDate " +
                    "FROM books b " +
                    "JOIN authors a ON b.authorId = a.authorId " +
                    "LEFT JOIN user_books ub ON b.bookId = ub.bookId AND ub.userId = ? " +
                    "ORDER BY b.title";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, currentUserId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    books.add(new Book(
                            rs.getInt("bookId"),
                            rs.getInt("authorId"),
                            rs.getString("title"),
                            rs.getString("authorName"),
                            rs.getInt("year"),
                            rs.getInt("numberOfPages"),
                            rs.getString("cover"),
                            rs.getString("about"),
                            rs.getInt("readStatus"),
                            rs.getInt("rating"),
                            rs.getString("comments"),
                            rs.getDate("releaseDate")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // Get all authors
    public static List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM authors ORDER BY surname, name";

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    authors.add(new Author(
                            rs.getInt("authorId"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("website")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return authors;
    }

    // Add author
    public static boolean addAuthor(String name, String surname, String website) {
        try (Connection conn = getConnection()) {
            String query = "INSERT INTO authors (name, surname, website) VALUES (?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, surname);
                stmt.setString(3, website);

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Legacy method for backward compatibility
     */
    public static boolean addBook(int authorId, String title, int year, int numberOfPages, String cover, String about) {
        return addBookComplete(authorId, title, year, numberOfPages, cover, about, 0, 0, "");
    }

    /**
     * Get existing author ID or create new author if doesn't exist
     */
    public static int getOrCreateAuthor(String name, String surname, String website) {
        try (Connection conn = getConnection()) {
            // First, check if author already exists
            String checkQuery = "SELECT authorId FROM authors WHERE name = ? AND surname = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, name);
                checkStmt.setString(2, surname);

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    // Author exists, return existing ID
                    System.out.println("Found existing author: " + name + " " + surname);
                    return rs.getInt("authorId");
                }
            }

            // Author doesn't exist, create new one
            String insertQuery = "INSERT INTO authors (name, surname, website) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, surname);
                insertStmt.setString(3, website.isEmpty() ? null : website);

                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int newAuthorId = generatedKeys.getInt(1);
                        System.out.println("Created new author: " + name + " " + surname + " with ID: " + newAuthorId);
                        return newAuthorId;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error in getOrCreateAuthor: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Add a book to the catalog AND add it to current user's library
     * Compatible with existing UI code (without Date parameter)
     */
    public static boolean addBookComplete(int authorId, String title, int year, int numberOfPages,
                                          String cover, String about, int readStatus, int rating, String comments) {
        if (currentUserId == -1) {
            System.err.println("❌ No user logged in!");
            return false;
        }

        // Calculate releaseDate based on readStatus
        Date releaseDate = null;
        if (readStatus == 3) { // Want to read
            // For demo purposes, set a future date (you can modify this logic)
            long currentTime = System.currentTimeMillis();
            long oneWeekFromNow = currentTime + (7 * 24 * 60 * 60 * 1000); // Add 1 week
            releaseDate = new Date(oneWeekFromNow);
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Step 1: Check if book already exists in catalog
            int bookId = -1;
            String checkBookQuery = "SELECT bookId FROM books WHERE title = ? AND authorId = ? AND year = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkBookQuery)) {
                checkStmt.setString(1, title);
                checkStmt.setInt(2, authorId);
                checkStmt.setInt(3, year);

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    bookId = rs.getInt("bookId");
                    System.out.println("📚 Book already exists in catalog: " + title);
                }
            }

            // Step 2: If book doesn't exist, add to catalog
            if (bookId == -1) {
                String insertBookQuery = "INSERT INTO books (authorId, title, year, numberOfPages, cover, about) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertBookQuery, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setInt(1, authorId);
                    insertStmt.setString(2, title);
                    insertStmt.setInt(3, year);
                    insertStmt.setInt(4, numberOfPages);
                    insertStmt.setString(5, cover.isEmpty() ? null : cover);
                    insertStmt.setString(6, about.isEmpty() ? null : about);

                    int result = insertStmt.executeUpdate();
                    if (result > 0) {
                        ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            bookId = generatedKeys.getInt(1);
                            System.out.println("📚 Added book to catalog: " + title);
                        }
                    }
                }
            }

            // Step 3: Add to current user's library
            if (bookId != -1) {
                String insertUserBookQuery = "INSERT INTO user_books (userId, bookId, readStatus, rating, comments, releaseDate) " +
                        "VALUES (?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "readStatus = VALUES(readStatus), " +
                        "rating = VALUES(rating), " +
                        "comments = VALUES(comments), " +
                        "releaseDate = VALUES(releaseDate)";

                try (PreparedStatement userStmt = conn.prepareStatement(insertUserBookQuery)) {
                    userStmt.setInt(1, currentUserId);
                    userStmt.setInt(2, bookId);
                    userStmt.setInt(3, readStatus);
                    userStmt.setInt(4, rating);
                    userStmt.setString(5, comments.isEmpty() ? null : comments);
                    userStmt.setDate(6, releaseDate);

                    userStmt.executeUpdate();
                    System.out.println("📖 Added book to user " + currentUserId + "'s library: " + title);
                }
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove book from CURRENT USER'S library only (not from catalog)
     */
    public static boolean deleteBook(int bookId) {
        if (currentUserId == -1) {
            System.err.println("❌ No user logged in!");
            return false;
        }

        try (Connection conn = getConnection()) {
            String query = "DELETE FROM user_books WHERE bookId = ? AND userId = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, bookId);
                stmt.setInt(2, currentUserId);

                int result = stmt.executeUpdate();
                System.out.println("📚 Removed book from user " + currentUserId + "'s library");
                return result > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get book info with current user's personal data
     */
    public static Book getBookInfo(int bookId) {
        if (currentUserId == -1) {
            System.err.println("❌ No user logged in!");
            return null;
        }

        try (Connection conn = getConnection()) {
            String query = "SELECT b.bookId, b.authorId, b.title, b.year, b.numberOfPages, b.cover, b.about, " +
                    "CONCAT(a.name, ' ', a.surname) as authorName, " +
                    "COALESCE(ub.readStatus, 0) as readStatus, " +
                    "COALESCE(ub.rating, 0) as rating, " +
                    "ub.comments, " +
                    "ub.releaseDate " +
                    "FROM books b " +
                    "JOIN authors a ON b.authorId = a.authorId " +
                    "LEFT JOIN user_books ub ON b.bookId = ub.bookId AND ub.userId = ? " +
                    "WHERE b.bookId = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, currentUserId);
                stmt.setInt(2, bookId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new Book(
                            rs.getInt("bookId"),
                            rs.getInt("authorId"),
                            rs.getString("title"),
                            rs.getString("authorName"),
                            rs.getInt("year"),
                            rs.getInt("numberOfPages"),
                            rs.getString("cover"),
                            rs.getString("about"),
                            rs.getInt("readStatus"),
                            rs.getInt("rating"),
                            rs.getString("comments"),
                            rs.getDate("releaseDate")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Search authors
    public static List<Author> searchAuthors(String searchTerm) {
        List<Author> authors = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM authors WHERE name LIKE ? OR surname LIKE ? ORDER BY surname, name";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                String searchPattern = "%" + searchTerm + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    authors.add(new Author(
                            rs.getInt("authorId"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("website")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return authors;
    }

    /**
     * Update user's personal book data
     */
    public static boolean updateBook(int bookId, String title, int year, int numberOfPages,
                                     String about, int readStatus, int rating, String comments) {
        if (currentUserId == -1) {
            System.err.println("❌ No user logged in!");
            return false;
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Update book catalog (title, year, pages, about)
            String updateBookQuery = "UPDATE books SET title = ?, year = ?, numberOfPages = ?, about = ? WHERE bookId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateBookQuery)) {
                stmt.setString(1, title);
                stmt.setInt(2, year);
                stmt.setInt(3, numberOfPages);
                stmt.setString(4, about);
                stmt.setInt(5, bookId);
                stmt.executeUpdate();
            }

            // Update user's personal data
            String updateUserBookQuery = "INSERT INTO user_books (userId, bookId, readStatus, rating, comments) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "readStatus = VALUES(readStatus), " +
                    "rating = VALUES(rating), " +
                    "comments = VALUES(comments)";

            try (PreparedStatement stmt = conn.prepareStatement(updateUserBookQuery)) {
                stmt.setInt(1, currentUserId);
                stmt.setInt(2, bookId);
                stmt.setInt(3, readStatus);
                stmt.setInt(4, rating);
                stmt.setString(5, comments);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get current user's favorite books (rating >= 4)
     */
    public static List<Book> getFavoriteBooks() {
        List<Book> books = new ArrayList<>();

        if (currentUserId == -1) {
            System.err.println("❌ No user logged in!");
            return books;
        }

        try (Connection conn = getConnection()) {
            String query = "SELECT b.bookId, b.authorId, b.title, b.year, b.numberOfPages, b.cover, b.about, " +
                    "CONCAT(a.name, ' ', a.surname) as authorName, " +
                    "ub.readStatus, ub.rating, ub.comments, ub.releaseDate " +
                    "FROM books b " +
                    "JOIN authors a ON b.authorId = a.authorId " +
                    "JOIN user_books ub ON b.bookId = ub.bookId " +
                    "WHERE ub.userId = ? AND ub.rating >= 4 " +
                    "ORDER BY ub.rating DESC, b.title";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, currentUserId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    books.add(new Book(
                            rs.getInt("bookId"),
                            rs.getInt("authorId"),
                            rs.getString("title"),
                            rs.getString("authorName"),
                            rs.getInt("year"),
                            rs.getInt("numberOfPages"),
                            rs.getString("cover"),
                            rs.getString("about"),
                            rs.getInt("readStatus"),
                            rs.getInt("rating"),
                            rs.getString("comments"),
                            rs.getDate("releaseDate")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    /**
     * Get current user's favorite authors (authors with books rated >= 4)
     */
    public static List<Author> getFavoriteAuthors() {
        List<Author> authors = new ArrayList<>();

        if (currentUserId == -1) {
            System.err.println("❌ No user logged in!");
            return authors;
        }

        try (Connection conn = getConnection()) {
            String query = "SELECT DISTINCT a.authorId, a.name, a.surname, a.website " +
                    "FROM authors a " +
                    "JOIN books b ON a.authorId = b.authorId " +
                    "JOIN user_books ub ON b.bookId = ub.bookId " +
                    "WHERE ub.userId = ? AND ub.rating >= 4 " +
                    "ORDER BY a.surname, a.name";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, currentUserId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    authors.add(new Author(
                            rs.getInt("authorId"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("website")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return authors;
    }

    /**
     * Get current user's unread books (readStatus = 0 or 2)
     */
    public static List<Book> getUnreadBooks() {
        List<Book> books = new ArrayList<>();

        if (currentUserId == -1) {
            System.err.println("❌ No user logged in!");
            return books;
        }

        try (Connection conn = getConnection()) {
            String query = "SELECT b.bookId, b.authorId, b.title, b.year, b.numberOfPages, b.cover, b.about, " +
                    "CONCAT(a.name, ' ', a.surname) as authorName, " +
                    "COALESCE(ub.readStatus, 0) as readStatus, " +
                    "COALESCE(ub.rating, 0) as rating, " +
                    "ub.comments, ub.releaseDate " +
                    "FROM books b " +
                    "JOIN authors a ON b.authorId = a.authorId " +
                    "LEFT JOIN user_books ub ON b.bookId = ub.bookId AND ub.userId = ? " +
                    "WHERE COALESCE(ub.readStatus, 0) IN (0, 2) " +
                    "ORDER BY b.title";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, currentUserId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    books.add(new Book(
                            rs.getInt("bookId"),
                            rs.getInt("authorId"),
                            rs.getString("title"),
                            rs.getString("authorName"),
                            rs.getInt("year"),
                            rs.getInt("numberOfPages"),
                            rs.getString("cover"),
                            rs.getString("about"),
                            rs.getInt("readStatus"),
                            rs.getInt("rating"),
                            rs.getString("comments"),
                            rs.getDate("releaseDate")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    /**
     * Get current user's upcoming releases (books with future release dates)
     */
    public static List<Book> getUpcomingReleases() {
        List<Book> books = new ArrayList<>();

        if (currentUserId == -1) {
            System.err.println("❌ No user logged in!");
            return books;
        }

        try (Connection conn = getConnection()) {
            String query = "SELECT b.bookId, b.authorId, b.title, b.year, b.numberOfPages, b.cover, b.about, " +
                    "CONCAT(a.name, ' ', a.surname) as authorName, " +
                    "ub.readStatus, ub.rating, ub.comments, ub.releaseDate " +
                    "FROM books b " +
                    "JOIN authors a ON b.authorId = a.authorId " +
                    "JOIN user_books ub ON b.bookId = ub.bookId " +
                    "WHERE ub.userId = ? AND ub.releaseDate >= CURDATE() AND ub.readStatus = 3 " +
                    "ORDER BY ub.releaseDate";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, currentUserId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    books.add(new Book(
                            rs.getInt("bookId"),
                            rs.getInt("authorId"),
                            rs.getString("title"),
                            rs.getString("authorName"),
                            rs.getInt("year"),
                            rs.getInt("numberOfPages"),
                            rs.getString("cover"),
                            rs.getString("about"),
                            rs.getInt("readStatus"),
                            rs.getInt("rating"),
                            rs.getString("comments"),
                            rs.getDate("releaseDate")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    /**
     * Update current user's read status for a book
     */
    public static boolean updateReadStatus(int bookId, int readStatus) {
        if (currentUserId == -1) {
            System.err.println("❌ No user logged in!");
            return false;
        }

        try (Connection conn = getConnection()) {
            String query = "INSERT INTO user_books (userId, bookId, readStatus) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE readStatus = VALUES(readStatus)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, currentUserId);
                stmt.setInt(2, bookId);
                stmt.setInt(3, readStatus);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update current user's rating for a book
     */
    public static boolean updateRating(int bookId, int rating) {
        if (currentUserId == -1) {
            System.err.println("❌ No user logged in!");
            return false;
        }

        try (Connection conn = getConnection()) {
            String query = "INSERT INTO user_books (userId, bookId, rating) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE rating = VALUES(rating)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, currentUserId);
                stmt.setInt(2, bookId);
                stmt.setInt(3, rating);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update book cover in the catalog (affects all users)
     */
    public static boolean updateBookCover(int bookId, String coverPath) {
        try (Connection conn = getConnection()) {
            String query = "UPDATE books SET cover = ? WHERE bookId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, coverPath);
                stmt.setInt(2, bookId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Validate user login and return user type
     */
    public static int validateLogin(String username, String password) {
        try (Connection conn = getConnection()) {
            String query = "SELECT userId, userType FROM userinfo WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("userId");
                    int userType = rs.getInt("userType");

                    // Set the current user
                    setCurrentUser(userId);

                    return userType;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Login failed
    }
}