import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mylibrary";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "58014833batU"; // Replace with your MySQL password

    // Get database connection
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    // Get all books
    public static List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String query = "SELECT b.*, CONCAT(a.name, ' ', a.surname) as authorName " +
                    "FROM books b JOIN authors a ON b.authorId = a.authorId";

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

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

    // Add book (original method)
    public static boolean addBook(int authorId, String title, int year, int numberOfPages, String cover, String about) {
        try (Connection conn = getConnection()) {
            String query = "INSERT INTO books (authorId, title, year, numberOfPages, cover, about) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, authorId);
                stmt.setString(2, title);
                stmt.setInt(3, year);
                stmt.setInt(4, numberOfPages);
                stmt.setString(5, cover);
                stmt.setString(6, about);

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get existing author ID or create new author if doesn't exist
     * @param name Author's first name
     * @param surname Author's last name
     * @param website Author's website (can be empty)
     * @return authorId if found/created, -1 if error
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
     * Add a complete book with all attributes
     * @param authorId The author's ID
     * @param title Book title
     * @param year Publication year
     * @param numberOfPages Number of pages
     * @param cover Cover image path
     * @param about Book description
     * @param readStatus Reading status (0-3)
     * @param rating User rating (0-5)
     * @param comments User comments
     * @return true if successful, false otherwise
     */
    public static boolean addBookComplete(int authorId, String title, int year, int numberOfPages,
                                          String cover, String about, int readStatus, int rating, String comments) {
        try (Connection conn = getConnection()) {
            String query = "INSERT INTO books (authorId, title, year, numberOfPages, cover, about, readStatus, rating, comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, authorId);
                stmt.setString(2, title);
                stmt.setInt(3, year);
                stmt.setInt(4, numberOfPages);
                stmt.setString(5, cover.isEmpty() ? null : cover);
                stmt.setString(6, about.isEmpty() ? null : about);
                stmt.setInt(7, readStatus);
                stmt.setInt(8, rating);
                stmt.setString(9, comments.isEmpty() ? null : comments);

                int result = stmt.executeUpdate();
                System.out.println("Book added successfully: " + title);
                return result > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete book
    public static boolean deleteBook(int bookId) {
        try (Connection conn = getConnection()) {
            String query = "DELETE FROM books WHERE bookId = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, bookId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get book info by ID
    public static Book getBookInfo(int bookId) {
        try (Connection conn = getConnection()) {
            String query = "SELECT b.*, CONCAT(a.name, ' ', a.surname) as authorName " +
                    "FROM books b JOIN authors a ON b.authorId = a.authorId WHERE b.bookId = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, bookId);
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

    // Update book
    public static boolean updateBook(int bookId, String title, int year, int numberOfPages,
                                     String about, int readStatus, int rating, String comments) {
        try (Connection conn = getConnection()) {
            String query = "UPDATE books SET title = ?, year = ?, numberOfPages = ?, about = ?, readStatus = ?, rating = ?, comments = ? WHERE bookId = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, title);
                stmt.setInt(2, year);
                stmt.setInt(3, numberOfPages);
                stmt.setString(4, about);
                stmt.setInt(5, readStatus);
                stmt.setInt(6, rating);
                stmt.setString(7, comments);
                stmt.setInt(8, bookId);

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get favorite books (rating >= 4)
    public static List<Book> getFavoriteBooks() {
        List<Book> books = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String query = "SELECT b.*, CONCAT(a.name, ' ', a.surname) as authorName " +
                    "FROM books b JOIN authors a ON b.authorId = a.authorId WHERE b.rating >= 4 ORDER BY b.rating DESC";

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

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

    // Get favorite authors (authors with books rated >= 4)
    public static List<Author> getFavoriteAuthors() {
        List<Author> authors = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String query = "SELECT DISTINCT a.* FROM authors a " +
                    "JOIN books b ON a.authorId = b.authorId " +
                    "WHERE b.rating >= 4 ORDER BY a.surname, a.name";

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

    // Get unread books
    public static List<Book> getUnreadBooks() {
        List<Book> books = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String query = "SELECT b.*, CONCAT(a.name, ' ', a.surname) as authorName " +
                    "FROM books b JOIN authors a ON b.authorId = a.authorId WHERE b.readStatus = 0";

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

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

    // Get upcoming releases
    public static List<Book> getUpcomingReleases() {
        List<Book> books = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String query = "SELECT b.*, CONCAT(a.name, ' ', a.surname) as authorName " +
                    "FROM books b JOIN authors a ON b.authorId = a.authorId " +
                    "WHERE b.releaseDate >= CURDATE() ORDER BY b.releaseDate";

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

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

    // Update read status
    public static boolean updateReadStatus(int bookId, int readStatus) {
        try (Connection conn = getConnection()) {
            String query = "UPDATE books SET readStatus = ? WHERE bookId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, readStatus);
                stmt.setInt(2, bookId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update book rating
    public static boolean updateRating(int bookId, int rating) {
        try (Connection conn = getConnection()) {
            String query = "UPDATE books SET rating = ? WHERE bookId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, rating);
                stmt.setInt(2, bookId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update book cover
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
}