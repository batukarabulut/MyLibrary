import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Type2MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private JTextArea bookDetailsArea;

    public Type2MainFrame() {
        initializeComponents();
        setupLayout();
        loadData();
        addEventListeners();

        setTitle("MyLibrary - User Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();

        // Books table (read-only for users)
        String[] bookColumns = {"ID", "Title", "Author", "Year", "Pages", "Status", "Rating"};
        booksTableModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only for users
            }
        };
        booksTable = new JTable(booksTableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        bookDetailsArea = new JTextArea();
        bookDetailsArea.setEditable(false);
        bookDetailsArea.setFont(new Font("Arial", Font.PLAIN, 12));
        bookDetailsArea.setLineWrap(true);
        bookDetailsArea.setWrapStyleWord(true);
    }

    private void setupLayout() {
        // Browse Books Panel
        JPanel browsePanel = createBrowsePanel();
        tabbedPane.addTab("📚 Browse Books", browsePanel);

        // My Reading Panel
        JPanel readingPanel = createMyReadingPanel();
        tabbedPane.addTab("📖 My Reading", readingPanel);

        // Favorites Panel
        JPanel favoritesPanel = createFavoritesPanel();
        tabbedPane.addTab("⭐ Favorites", favoritesPanel);

        // Authors Panel
        JPanel authorsPanel = createAuthorsPanel();
        tabbedPane.addTab("👥 Authors", authorsPanel);

        // Notifications Panel
        JPanel notificationsPanel = createNotificationsPanel();
        tabbedPane.addTab("🔔 Notifications", notificationsPanel);

        // Book Cover Display Panel
        BookCoverDisplayPanel coverPanel = new BookCoverDisplayPanel();
        tabbedPane.addTab("🖼️ Book Covers", coverPanel);

        add(tabbedPane);
    }

    private JPanel createBrowsePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table with books
        JScrollPane tableScrollPane = new JScrollPane(booksTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 300));

        // Book details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("📖 Book Details"));
        detailsPanel.setPreferredSize(new Dimension(300, 300));

        JScrollPane detailsScrollPane = new JScrollPane(bookDetailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

        // Button panel for user actions
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh");
        JButton markReadButton = new JButton("Mark as Read");
        JButton markReadingButton = new JButton("Currently Reading");
        JButton markWantButton = new JButton("Want to Read");
        JButton rateButton = new JButton("Rate Book");

        refreshButton.addActionListener(e -> loadBooksData());
        markReadButton.addActionListener(e -> updateReadStatus(1)); // Read
        markReadingButton.addActionListener(e -> updateReadStatus(2)); // Reading
        markWantButton.addActionListener(e -> updateReadStatus(3)); // Want to read
        rateButton.addActionListener(e -> rateBook());

        buttonPanel.add(refreshButton);
        buttonPanel.add(markReadButton);
        buttonPanel.add(markReadingButton);
        buttonPanel.add(markWantButton);
        buttonPanel.add(rateButton);

        // Layout
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(detailsPanel, BorderLayout.EAST);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMyReadingPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        // Read Books
        JPanel readPanel = new JPanel(new BorderLayout());
        readPanel.setBorder(BorderFactory.createTitledBorder("✅ Books I've Read"));

        JList<Book> readList = new JList<>();
        JScrollPane readScroll = new JScrollPane(readList);

        JButton loadReadButton = new JButton("Load Read Books");
        loadReadButton.addActionListener(e -> {
            List<Book> allBooks = DatabaseHelper.getAllBooks();
            Book[] readBooks = allBooks.stream()
                    .filter(book -> book.getReadStatus() == 1)
                    .toArray(Book[]::new);
            readList.setListData(readBooks);
        });

        readPanel.add(readScroll, BorderLayout.CENTER);
        readPanel.add(loadReadButton, BorderLayout.SOUTH);

        // Currently Reading
        JPanel readingPanel = new JPanel(new BorderLayout());
        readingPanel.setBorder(BorderFactory.createTitledBorder("📖 Currently Reading"));

        JList<Book> readingList = new JList<>();
        JScrollPane readingScroll = new JScrollPane(readingList);

        JButton loadReadingButton = new JButton("Load Currently Reading");
        loadReadingButton.addActionListener(e -> {
            List<Book> allBooks = DatabaseHelper.getAllBooks();
            Book[] readingBooks = allBooks.stream()
                    .filter(book -> book.getReadStatus() == 2)
                    .toArray(Book[]::new);
            readingList.setListData(readingBooks);
        });

        readingPanel.add(readingScroll, BorderLayout.CENTER);
        readingPanel.add(loadReadingButton, BorderLayout.SOUTH);

        // Want to Read
        JPanel wantPanel = new JPanel(new BorderLayout());
        wantPanel.setBorder(BorderFactory.createTitledBorder("🎯 Want to Read"));

        JList<Book> wantList = new JList<>();
        JScrollPane wantScroll = new JScrollPane(wantList);

        JButton loadWantButton = new JButton("Load Want to Read");
        loadWantButton.addActionListener(e -> {
            List<Book> allBooks = DatabaseHelper.getAllBooks();
            Book[] wantBooks = allBooks.stream()
                    .filter(book -> book.getReadStatus() == 3)
                    .toArray(Book[]::new);
            wantList.setListData(wantBooks);
        });

        wantPanel.add(wantScroll, BorderLayout.CENTER);
        wantPanel.add(loadWantButton, BorderLayout.SOUTH);

        // Unread Books
        JPanel unreadPanel = new JPanel(new BorderLayout());
        unreadPanel.setBorder(BorderFactory.createTitledBorder("📚 Unread Books"));

        JList<Book> unreadList = new JList<>();
        JScrollPane unreadScroll = new JScrollPane(unreadList);

        JButton loadUnreadButton = new JButton("Load Unread Books");
        loadUnreadButton.addActionListener(e -> {
            List<Book> unreadBooks = DatabaseHelper.getUnreadBooks();
            unreadList.setListData(unreadBooks.toArray(new Book[0]));
        });

        unreadPanel.add(unreadScroll, BorderLayout.CENTER);
        unreadPanel.add(loadUnreadButton, BorderLayout.SOUTH);

        panel.add(readPanel);
        panel.add(readingPanel);
        panel.add(wantPanel);
        panel.add(unreadPanel);

        return panel;
    }

    private JPanel createFavoritesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Favorite Books
        JPanel favBooksPanel = new JPanel(new BorderLayout());
        favBooksPanel.setBorder(BorderFactory.createTitledBorder("⭐ My Favorite Books"));

        JList<Book> favBooksList = new JList<>();
        JScrollPane favBooksScroll = new JScrollPane(favBooksList);

        JPanel favBooksButtonPanel = new JPanel(new FlowLayout());
        JButton loadFavBooksButton = new JButton("Load Favorites");
        JButton showDetailsButton = new JButton("Show Details");

        loadFavBooksButton.addActionListener(e -> {
            List<Book> favBooks = DatabaseHelper.getFavoriteBooks();
            favBooksList.setListData(favBooks.toArray(new Book[0]));
        });

        showDetailsButton.addActionListener(e -> {
            Book selectedBook = favBooksList.getSelectedValue();
            if (selectedBook != null) {
                showBookDetails(selectedBook);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book first!");
            }
        });

        favBooksButtonPanel.add(loadFavBooksButton);
        favBooksButtonPanel.add(showDetailsButton);

        favBooksPanel.add(favBooksScroll, BorderLayout.CENTER);
        favBooksPanel.add(favBooksButtonPanel, BorderLayout.SOUTH);

        // Favorite Authors
        JPanel favAuthorsPanel = new JPanel(new BorderLayout());
        favAuthorsPanel.setBorder(BorderFactory.createTitledBorder("⭐ My Favorite Authors"));

        JList<Author> favAuthorsList = new JList<>();
        JScrollPane favAuthorsScroll = new JScrollPane(favAuthorsList);

        JPanel favAuthorsButtonPanel = new JPanel(new FlowLayout());
        JButton loadFavAuthorsButton = new JButton("Load Favorite Authors");
        JButton showAuthorInfoButton = new JButton("Show Author Info");

        loadFavAuthorsButton.addActionListener(e -> {
            List<Author> favAuthors = DatabaseHelper.getFavoriteAuthors();
            favAuthorsList.setListData(favAuthors.toArray(new Author[0]));
        });

        showAuthorInfoButton.addActionListener(e -> {
            Author selectedAuthor = favAuthorsList.getSelectedValue();
            if (selectedAuthor != null) {
                showAuthorInfo(selectedAuthor);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an author first!");
            }
        });

        favAuthorsButtonPanel.add(loadFavAuthorsButton);
        favAuthorsButtonPanel.add(showAuthorInfoButton);

        favAuthorsPanel.add(favAuthorsScroll, BorderLayout.CENTER);
        favAuthorsPanel.add(favAuthorsButtonPanel, BorderLayout.SOUTH);

        panel.add(favBooksPanel);
        panel.add(favAuthorsPanel);

        return panel;
    }

    private JPanel createAuthorsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search Authors");

        searchButton.addActionListener(e -> searchAuthors(searchField.getText()));

        searchPanel.add(new JLabel("Search Authors:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Authors list
        JList<Author> authorsList = new JList<>();
        JScrollPane authorsScroll = new JScrollPane(authorsList);

        // Author info area
        JTextArea authorInfoArea = new JTextArea();
        authorInfoArea.setEditable(false);
        authorInfoArea.setLineWrap(true);
        authorInfoArea.setWrapStyleWord(true);
        JScrollPane authorInfoScroll = new JScrollPane(authorInfoArea);
        authorInfoScroll.setPreferredSize(new Dimension(300, 200));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loadAllAuthorsButton = new JButton("Load All Authors");
        JButton showAuthorBooksButton = new JButton("Show Author's Books");

        loadAllAuthorsButton.addActionListener(e -> {
            List<Author> allAuthors = DatabaseHelper.getAllAuthors();
            authorsList.setListData(allAuthors.toArray(new Author[0]));
        });

        showAuthorBooksButton.addActionListener(e -> {
            Author selectedAuthor = authorsList.getSelectedValue();
            if (selectedAuthor != null) {
                showAuthorBooks(selectedAuthor, authorInfoArea);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an author first!");
            }
        });

        // Add selection listener to show author info
        authorsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Author selectedAuthor = authorsList.getSelectedValue();
                if (selectedAuthor != null) {
                    String info = String.format(
                            "📝 Author Information\n\n" +
                                    "Name: %s\n" +
                                    "Website: %s\n",
                            selectedAuthor.getFullName(),
                            selectedAuthor.getWebsite() != null ? selectedAuthor.getWebsite() : "No website available"
                    );
                    authorInfoArea.setText(info);
                }
            }
        });

        buttonPanel.add(loadAllAuthorsButton);
        buttonPanel.add(showAuthorBooksButton);

        // Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, authorsScroll, authorInfoScroll);
        splitPane.setDividerLocation(400);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Upcoming Releases
        JPanel releasesPanel = new JPanel(new BorderLayout());
        releasesPanel.setBorder(BorderFactory.createTitledBorder("🔔 Upcoming Book Releases"));

        JList<Book> releasesList = new JList<>();
        JScrollPane releasesScroll = new JScrollPane(releasesList);

        JButton checkReleasesButton = new JButton("Check Upcoming Releases");
        checkReleasesButton.addActionListener(e -> {
            List<Book> upcomingBooks = DatabaseHelper.getUpcomingReleases();
            releasesList.setListData(upcomingBooks.toArray(new Book[0]));

            if (upcomingBooks.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No upcoming releases found!");
            } else {
                JOptionPane.showMessageDialog(this,
                        String.format("Found %d upcoming releases!", upcomingBooks.size()));
            }
        });

        releasesPanel.add(releasesScroll, BorderLayout.CENTER);
        releasesPanel.add(checkReleasesButton, BorderLayout.SOUTH);

        // Reading Statistics
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("📊 My Reading Statistics"));

        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane statsScroll = new JScrollPane(statsArea);

        JButton generateStatsButton = new JButton("Generate My Statistics");
        generateStatsButton.addActionListener(e -> {
            List<Book> allBooks = DatabaseHelper.getAllBooks();

            long totalBooks = allBooks.size();
            long readBooks = allBooks.stream().filter(b -> b.getReadStatus() == 1).count();
            long readingBooks = allBooks.stream().filter(b -> b.getReadStatus() == 2).count();
            long wantToReadBooks = allBooks.stream().filter(b -> b.getReadStatus() == 3).count();
            long unreadBooks = allBooks.stream().filter(b -> b.getReadStatus() == 0).count();

            long ratedBooks = allBooks.stream().filter(b -> b.getRating() > 0).count();
            double avgRating = allBooks.stream()
                    .mapToInt(Book::getRating)
                    .filter(r -> r > 0)
                    .average()
                    .orElse(0.0);

            long favoriteBooks = allBooks.stream().filter(b -> b.getRating() >= 4).count();

            int totalPages = allBooks.stream()
                    .filter(b -> b.getReadStatus() == 1)
                    .mapToInt(Book::getNumberOfPages)
                    .sum();

            String stats = String.format(
                    "📚 MY READING STATISTICS\n\n" +
                            "📖 Total Books in Library: %d\n" +
                            "✅ Books I've Read: %d (%.1f%%)\n" +
                            "📖 Currently Reading: %d\n" +
                            "🎯 Want to Read: %d\n" +
                            "📚 Unread: %d\n\n" +
                            "⭐ RATINGS & FAVORITES\n" +
                            "⭐ Books I've Rated: %d\n" +
                            "📊 My Average Rating: %.1f/5 stars\n" +
                            "💖 Favorite Books (4+ stars): %d\n\n" +
                            "📄 READING VOLUME\n" +
                            "📄 Total Pages Read: %,d pages\n" +
                            "📖 Average Pages per Book: %.0f pages\n",
                    totalBooks, readBooks, totalBooks > 0 ? (readBooks * 100.0 / totalBooks) : 0,
                    readingBooks, wantToReadBooks, unreadBooks,
                    ratedBooks, avgRating, favoriteBooks,
                    totalPages, readBooks > 0 ? (totalPages / (double) readBooks) : 0
            );

            statsArea.setText(stats);
        });

        statsPanel.add(statsScroll, BorderLayout.CENTER);
        statsPanel.add(generateStatsButton, BorderLayout.SOUTH);

        panel.add(releasesPanel);
        panel.add(statsPanel);

        return panel;
    }

    private void addEventListeners() {
        // Table selection listener to show book details
        booksTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = booksTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int bookId = (Integer) booksTableModel.getValueAt(selectedRow, 0);
                    Book book = DatabaseHelper.getBookInfo(bookId);
                    if (book != null) {
                        displayBookDetails(book);
                    }
                }
            }
        });
    }

    private void loadData() {
        loadBooksData();
    }

    private void loadBooksData() {
        booksTableModel.setRowCount(0);
        List<Book> books = DatabaseHelper.getAllBooks();

        for (Book book : books) {
            Object[] row = {
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthorName(),
                    book.getYear(),
                    book.getNumberOfPages(),
                    book.getReadStatusText(),
                    book.getRatingStars()
            };
            booksTableModel.addRow(row);
        }
    }

    private void displayBookDetails(Book book) {
        String details = String.format(
                "📖 BOOK DETAILS\n\n" +
                        "📚 Title: %s\n" +
                        "✍️ Author: %s\n" +
                        "📅 Year: %d\n" +
                        "📄 Pages: %d\n" +
                        "📊 Status: %s\n" +
                        "⭐ Rating: %s\n" +
                        "🖼️ Cover: %s\n\n" +
                        "📝 DESCRIPTION:\n%s\n\n" +
                        "💭 MY COMMENTS:\n%s",
                book.getTitle(),
                book.getAuthorName(),
                book.getYear(),
                book.getNumberOfPages(),
                book.getReadStatusText(),
                book.getRatingStars(),
                book.getCover() != null ? book.getCover() : "No cover available",
                book.getAbout() != null ? book.getAbout() : "No description available",
                book.getComments() != null && !book.getComments().isEmpty() ?
                        book.getComments() : "No comments yet"
        );

        bookDetailsArea.setText(details);
    }

    private void updateReadStatus(int newStatus) {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book first!");
            return;
        }

        int bookId = (Integer) booksTableModel.getValueAt(selectedRow, 0);
        boolean success = DatabaseHelper.updateReadStatus(bookId, newStatus);

        if (success) {
            String statusText = "";
            switch (newStatus) {
                case 1: statusText = "read"; break;
                case 2: statusText = "currently reading"; break;
                case 3: statusText = "want to read"; break;
            }
            JOptionPane.showMessageDialog(this, "Book marked as " + statusText + "!");
            loadBooksData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update book status!");
        }
    }

    private void rateBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book first!");
            return;
        }

        String ratingStr = JOptionPane.showInputDialog(
                this,
                "Rate this book (0-5 stars):",
                "Rate Book",
                JOptionPane.QUESTION_MESSAGE
        );

        if (ratingStr != null) {
            try {
                int rating = Integer.parseInt(ratingStr.trim());
                if (rating < 0 || rating > 5) {
                    JOptionPane.showMessageDialog(this, "Rating must be between 0 and 5!");
                    return;
                }

                int bookId = (Integer) booksTableModel.getValueAt(selectedRow, 0);
                boolean success = DatabaseHelper.updateRating(bookId, rating);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Book rated successfully!");
                    loadBooksData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to rate book!");
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number!");
            }
        }
    }

    private void searchAuthors(String searchTerm) {
        if (searchTerm.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term!");
            return;
        }

        List<Author> authors = DatabaseHelper.searchAuthors(searchTerm);

        if (authors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No authors found matching: " + searchTerm);
        } else {
            // Find the authors list in the authors panel and update it
            Component authorsPanel = tabbedPane.getComponentAt(3); // Authors tab
            updateAuthorsList(authorsPanel, authors);
        }
    }

    private void updateAuthorsList(Component panel, List<Author> authors) {
        // This is a simplified version - in a real implementation you'd need to
        // get reference to the actual JList component
        StringBuilder result = new StringBuilder("Search Results:\n\n");
        for (Author author : authors) {
            result.append(String.format("• %s (%s)\n",
                    author.getFullName(),
                    author.getWebsite() != null ? author.getWebsite() : "No website"));
        }

        JOptionPane.showMessageDialog(this, result.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showBookDetails(Book book) {
        String details = String.format(
                "📖 %s\n\n" +
                        "✍️ Author: %s\n" +
                        "📅 Year: %d\n" +
                        "📄 Pages: %d pages\n" +
                        "📊 Status: %s\n" +
                        "⭐ Rating: %s\n\n" +
                        "📝 Description:\n%s\n\n" +
                        "💭 Comments:\n%s",
                book.getTitle(),
                book.getAuthorName(),
                book.getYear(),
                book.getNumberOfPages(),
                book.getReadStatusText(),
                book.getRatingStars(),
                book.getAbout() != null ? book.getAbout() : "No description available",
                book.getComments() != null && !book.getComments().isEmpty() ?
                        book.getComments() : "No comments"
        );

        JOptionPane.showMessageDialog(this, details, "Book Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAuthorInfo(Author author) {
        String info = String.format(
                "👤 %s\n\n" +
                        "🌐 Website: %s\n",
                author.getFullName(),
                author.getWebsite() != null ? author.getWebsite() : "No website available"
        );

        JOptionPane.showMessageDialog(this, info, "Author Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAuthorBooks(Author author, JTextArea infoArea) {
        List<Book> allBooks = DatabaseHelper.getAllBooks();
        Book[] authorBooks = allBooks.stream()
                .filter(book -> book.getAuthorId() == author.getAuthorId())
                .toArray(Book[]::new);

        StringBuilder info = new StringBuilder();
        info.append(String.format("📚 Books by %s\n\n", author.getFullName()));

        if (authorBooks.length == 0) {
            info.append("No books found for this author.");
        } else {
            for (Book book : authorBooks) {
                info.append(String.format("• %s (%d) - %s %s\n",
                        book.getTitle(),
                        book.getYear(),
                        book.getReadStatusText(),
                        book.getRating() > 0 ? book.getRatingStars() : ""
                ));
            }
        }

        infoArea.setText(info.toString());
    }
}