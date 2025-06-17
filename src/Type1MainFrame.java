import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Type1MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable booksTable;
    private JTable authorsTable;
    private DefaultTableModel booksTableModel;
    private DefaultTableModel authorsTableModel;

    // Form components for adding/editing books
    private JTextField titleField, yearField, pagesField, coverField;
    private JTextArea aboutArea, commentsArea;
    private JComboBox<Author> authorComboBox;
    private JComboBox<String> statusComboBox;
    private JSpinner ratingSpinner;

    public Type1MainFrame() {
        initializeComponents();
        setupLayout();
        loadData();
        addEventListeners();

        setTitle("MyLibrary - Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();

        // Books table
        String[] bookColumns = {"ID", "Title", "Author", "Year", "Pages", "Status", "Rating"};
        booksTableModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        booksTable = new JTable(booksTableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Authors table
        String[] authorColumns = {"ID", "Name", "Surname", "Website"};
        authorsTableModel = new DefaultTableModel(authorColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        authorsTable = new JTable(authorsTableModel);

        // Form components
        titleField = new JTextField(20);
        yearField = new JTextField(10);
        pagesField = new JTextField(10);
        coverField = new JTextField(20);
        aboutArea = new JTextArea(3, 20);
        commentsArea = new JTextArea(2, 20);

        authorComboBox = new JComboBox<>();
        statusComboBox = new JComboBox<>(new String[]{"Not Read", "Read", "Reading", "Want to Read"});
        ratingSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));

        aboutArea.setLineWrap(true);
        aboutArea.setWrapStyleWord(true);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
    }

    private void setupLayout() {
        // Main Books Panel
        JPanel booksPanel = createBooksPanel();
        tabbedPane.addTab("📚 Books Management", booksPanel);

        // Authors Panel
        JPanel authorsPanel = createAuthorsPanel();
        tabbedPane.addTab("👥 Authors", authorsPanel);

        // Favorites Panel
        JPanel favoritesPanel = createFavoritesPanel();
        tabbedPane.addTab("⭐ Favorites", favoritesPanel);

        // Reports Panel
        JPanel reportsPanel = createReportsPanel();
        tabbedPane.addTab("📊 Reports", reportsPanel);

        // Book Cover Display Panel
        BookCoverDisplayPanel coverPanel = new BookCoverDisplayPanel();
        tabbedPane.addTab("🖼️ Book Covers", coverPanel);

        add(tabbedPane);
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table with scroll pane
        JScrollPane tableScrollPane = new JScrollPane(booksTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 300));

        // Form panel for adding/editing books
        JPanel formPanel = createBookFormPanel();

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Book");
        JButton updateButton = new JButton("Update Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addBook());
        updateButton.addActionListener(e -> updateBook());
        deleteButton.addActionListener(e -> deleteBook());
        refreshButton.addActionListener(e -> loadBooksData());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Layout
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.EAST);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBookFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Book Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        // Author Name
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Author Name:"), gbc);
        gbc.gridx = 1;
        JTextField authorNameField = new JTextField(15);
        panel.add(authorNameField, gbc);

        // Author Surname
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Author Surname:"), gbc);
        gbc.gridx = 1;
        JTextField authorSurnameField = new JTextField(15);
        panel.add(authorSurnameField, gbc);

        // Author Website (optional)
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Author Website:"), gbc);
        gbc.gridx = 1;
        JTextField authorWebsiteField = new JTextField(15);
        authorWebsiteField.setToolTipText("Optional - leave blank if unknown");
        panel.add(authorWebsiteField, gbc);

        // Year
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        panel.add(yearField, gbc);

        // Pages
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Pages:"), gbc);
        gbc.gridx = 1;
        panel.add(pagesField, gbc);

        // Cover
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Cover:"), gbc);
        gbc.gridx = 1;
        panel.add(coverField, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        panel.add(statusComboBox, gbc);

        // Rating
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("Rating:"), gbc);
        gbc.gridx = 1;
        panel.add(ratingSpinner, gbc);

        // About
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("About:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(aboutArea), gbc);

        // Comments
        gbc.gridx = 0; gbc.gridy = 10;
        panel.add(new JLabel("Comments:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(commentsArea), gbc);

        // Store references to author fields for access in addBook method
        panel.putClientProperty("authorNameField", authorNameField);
        panel.putClientProperty("authorSurnameField", authorSurnameField);
        panel.putClientProperty("authorWebsiteField", authorWebsiteField);

        return panel;
    }

    private JPanel createAuthorsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search Authors");
        searchButton.addActionListener(e -> searchAuthors(searchField.getText()));

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Table
        JScrollPane authorsScrollPane = new JScrollPane(authorsTable);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addAuthorButton = new JButton("Add Author");
        JButton refreshAuthorsButton = new JButton("Refresh");

        addAuthorButton.addActionListener(e -> addAuthor());
        refreshAuthorsButton.addActionListener(e -> loadAuthorsData());

        buttonPanel.add(addAuthorButton);
        buttonPanel.add(refreshAuthorsButton);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(authorsScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFavoritesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));

        // Favorite Books
        JPanel favBooksPanel = new JPanel(new BorderLayout());
        favBooksPanel.setBorder(BorderFactory.createTitledBorder("⭐ Favorite Books"));

        JList<Book> favBooksList = new JList<>();
        JScrollPane favBooksScroll = new JScrollPane(favBooksList);

        JButton loadFavBooksButton = new JButton("Load Favorite Books");
        loadFavBooksButton.addActionListener(e -> {
            List<Book> favBooks = DatabaseHelper.getFavoriteBooks();
            favBooksList.setListData(favBooks.toArray(new Book[0]));
        });

        favBooksPanel.add(favBooksScroll, BorderLayout.CENTER);
        favBooksPanel.add(loadFavBooksButton, BorderLayout.SOUTH);

        // Favorite Authors
        JPanel favAuthorsPanel = new JPanel(new BorderLayout());
        favAuthorsPanel.setBorder(BorderFactory.createTitledBorder("⭐ Favorite Authors"));

        JList<Author> favAuthorsList = new JList<>();
        JScrollPane favAuthorsScroll = new JScrollPane(favAuthorsList);

        JButton loadFavAuthorsButton = new JButton("Load Favorite Authors");
        loadFavAuthorsButton.addActionListener(e -> {
            List<Author> favAuthors = DatabaseHelper.getFavoriteAuthors();
            favAuthorsList.setListData(favAuthors.toArray(new Author[0]));
        });

        favAuthorsPanel.add(favAuthorsScroll, BorderLayout.CENTER);
        favAuthorsPanel.add(loadFavAuthorsButton, BorderLayout.SOUTH);

        panel.add(favBooksPanel);
        panel.add(favAuthorsPanel);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));

        // Unread Books
        JPanel unreadPanel = new JPanel(new BorderLayout());
        unreadPanel.setBorder(BorderFactory.createTitledBorder("📖 Unread Books"));

        JList<Book> unreadList = new JList<>();
        JScrollPane unreadScroll = new JScrollPane(unreadList);

        JButton loadUnreadButton = new JButton("Load Unread Books");
        loadUnreadButton.addActionListener(e -> {
            List<Book> unreadBooks = DatabaseHelper.getUnreadBooks();
            unreadList.setListData(unreadBooks.toArray(new Book[0]));
        });

        unreadPanel.add(unreadScroll, BorderLayout.CENTER);
        unreadPanel.add(loadUnreadButton, BorderLayout.SOUTH);

        // Upcoming Releases
        JPanel releasesPanel = new JPanel(new BorderLayout());
        releasesPanel.setBorder(BorderFactory.createTitledBorder("🔔 Upcoming Releases"));

        JList<Book> releasesList = new JList<>();
        JScrollPane releasesScroll = new JScrollPane(releasesList);

        JButton loadReleasesButton = new JButton("Check Releases");
        loadReleasesButton.addActionListener(e -> {
            List<Book> upcomingBooks = DatabaseHelper.getUpcomingReleases();
            releasesList.setListData(upcomingBooks.toArray(new Book[0]));
        });

        releasesPanel.add(releasesScroll, BorderLayout.CENTER);
        releasesPanel.add(loadReleasesButton, BorderLayout.SOUTH);

        // Statistics
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("📊 Statistics"));

        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        JScrollPane statsScroll = new JScrollPane(statsArea);

        JButton loadStatsButton = new JButton("Generate Statistics");
        loadStatsButton.addActionListener(e -> {
            List<Book> allBooks = DatabaseHelper.getAllBooks();
            List<Author> allAuthors = DatabaseHelper.getAllAuthors();

            long readBooks = allBooks.stream().filter(b -> b.getReadStatus() == 1).count();
            long unreadBooks = allBooks.stream().filter(b -> b.getReadStatus() == 0).count();
            double avgRating = allBooks.stream().mapToInt(Book::getRating).filter(r -> r > 0).average().orElse(0.0);

            String stats = String.format(
                    "📚 Total Books: %d\n" +
                            "👥 Total Authors: %d\n" +
                            "✅ Read Books: %d\n" +
                            "📖 Unread Books: %d\n" +
                            "⭐ Average Rating: %.1f/5\n",
                    allBooks.size(), allAuthors.size(), readBooks, unreadBooks, avgRating
            );

            statsArea.setText(stats);
        });

        statsPanel.add(statsScroll, BorderLayout.CENTER);
        statsPanel.add(loadStatsButton, BorderLayout.SOUTH);

        panel.add(unreadPanel);
        panel.add(releasesPanel);
        panel.add(statsPanel);

        return panel;
    }

    private void addEventListeners() {
        // Table selection listener
        booksTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = booksTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadBookToForm(selectedRow);
                }
            }
        });
    }

    private void loadData() {
        loadBooksData();
        loadAuthorsData();
        loadAuthorsComboBox();
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

    private void loadAuthorsData() {
        authorsTableModel.setRowCount(0);
        List<Author> authors = DatabaseHelper.getAllAuthors();

        for (Author author : authors) {
            Object[] row = {
                    author.getAuthorId(),
                    author.getName(),
                    author.getSurname(),
                    author.getWebsite()
            };
            authorsTableModel.addRow(row);
        }
    }

    private void loadAuthorsComboBox() {
        authorComboBox.removeAllItems();
        List<Author> authors = DatabaseHelper.getAllAuthors();
        for (Author author : authors) {
            authorComboBox.addItem(author);
        }
    }

    private void loadBookToForm(int selectedRow) {
        int bookId = (Integer) booksTableModel.getValueAt(selectedRow, 0);
        Book book = DatabaseHelper.getBookInfo(bookId);

        if (book != null) {
            titleField.setText(book.getTitle());
            yearField.setText(String.valueOf(book.getYear()));
            pagesField.setText(String.valueOf(book.getNumberOfPages()));
            coverField.setText(book.getCover());
            aboutArea.setText(book.getAbout());
            commentsArea.setText(book.getComments());
            statusComboBox.setSelectedIndex(book.getReadStatus());
            ratingSpinner.setValue(book.getRating());

            // Select the correct author
            for (int i = 0; i < authorComboBox.getItemCount(); i++) {
                Author author = authorComboBox.getItemAt(i);
                if (author.getAuthorId() == book.getAuthorId()) {
                    authorComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void addBook() {
        try {
            // Get form panel and extract author fields
            JPanel formPanel = (JPanel) ((JPanel) tabbedPane.getComponentAt(0)).getComponent(1);
            JTextField authorNameField = (JTextField) formPanel.getClientProperty("authorNameField");
            JTextField authorSurnameField = (JTextField) formPanel.getClientProperty("authorSurnameField");
            JTextField authorWebsiteField = (JTextField) formPanel.getClientProperty("authorWebsiteField");

            // Validate inputs
            String title = titleField.getText().trim();
            String authorName = authorNameField.getText().trim();
            String authorSurname = authorSurnameField.getText().trim();
            String authorWebsite = authorWebsiteField.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a book title!");
                return;
            }

            if (authorName.isEmpty() || authorSurname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both author name and surname!");
                return;
            }

            int year = Integer.parseInt(yearField.getText().trim());
            int pages = Integer.parseInt(pagesField.getText().trim());
            String cover = coverField.getText().trim();
            String about = aboutArea.getText().trim();
            int readStatus = statusComboBox.getSelectedIndex();
            int rating = (Integer) ratingSpinner.getValue();
            String comments = commentsArea.getText().trim();

            // Step 1: Check if author exists, if not create new one
            int authorId = DatabaseHelper.getOrCreateAuthor(authorName, authorSurname, authorWebsite);

            if (authorId == -1) {
                JOptionPane.showMessageDialog(this, "Failed to create or find author!");
                return;
            }

            // Step 2: Add the book with the authorId
            boolean success = DatabaseHelper.addBookComplete(
                    authorId, title, year, pages, cover, about, readStatus, rating, comments
            );

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Book added successfully!\n" +
                                "Title: " + title + "\n" +
                                "Author: " + authorName + " " + authorSurname);
                loadBooksData();
                loadAuthorsData(); // Refresh in case new author was added
                loadAuthorsComboBox();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add book!");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for year and pages!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding book: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to update!");
            return;
        }

        try {
            int bookId = (Integer) booksTableModel.getValueAt(selectedRow, 0);
            String title = titleField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            int pages = Integer.parseInt(pagesField.getText().trim());
            String about = aboutArea.getText().trim();
            int readStatus = statusComboBox.getSelectedIndex();
            int rating = (Integer) ratingSpinner.getValue();
            String comments = commentsArea.getText().trim();

            boolean success = DatabaseHelper.updateBook(
                    bookId, title, year, pages, about, readStatus, rating, comments
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Book updated successfully!");
                loadBooksData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update book!");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!");
        }
    }

    private void deleteBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this book?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int bookId = (Integer) booksTableModel.getValueAt(selectedRow, 0);
            boolean success = DatabaseHelper.deleteBook(bookId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                loadBooksData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete book!");
            }
        }
    }

    private void searchAuthors(String searchTerm) {
        if (searchTerm.trim().isEmpty()) {
            loadAuthorsData();
            return;
        }

        authorsTableModel.setRowCount(0);
        List<Author> authors = DatabaseHelper.searchAuthors(searchTerm);

        for (Author author : authors) {
            Object[] row = {
                    author.getAuthorId(),
                    author.getName(),
                    author.getSurname(),
                    author.getWebsite()
            };
            authorsTableModel.addRow(row);
        }

        if (authors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No authors found matching: " + searchTerm);
        }
    }

    private void addAuthor() {
        String name = JOptionPane.showInputDialog(this, "Enter author name:");
        if (name == null || name.trim().isEmpty()) return;

        String surname = JOptionPane.showInputDialog(this, "Enter author surname:");
        if (surname == null || surname.trim().isEmpty()) return;

        String website = JOptionPane.showInputDialog(this, "Enter author website (optional):");
        if (website == null) website = "";

        boolean success = DatabaseHelper.addAuthor(name.trim(), surname.trim(), website.trim());

        if (success) {
            JOptionPane.showMessageDialog(this, "Author added successfully!");
            loadAuthorsData();
            loadAuthorsComboBox();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add author!");
        }
    }

    private void clearForm() {
        titleField.setText("");
        yearField.setText("");
        pagesField.setText("");
        coverField.setText("");
        aboutArea.setText("");
        commentsArea.setText("");
        statusComboBox.setSelectedIndex(0);
        ratingSpinner.setValue(0);

        // Clear author fields
        try {
            JPanel formPanel = (JPanel) ((JPanel) tabbedPane.getComponentAt(0)).getComponent(1);
            JTextField authorNameField = (JTextField) formPanel.getClientProperty("authorNameField");
            JTextField authorSurnameField = (JTextField) formPanel.getClientProperty("authorSurnameField");
            JTextField authorWebsiteField = (JTextField) formPanel.getClientProperty("authorWebsiteField");

            if (authorNameField != null) authorNameField.setText("");
            if (authorSurnameField != null) authorSurnameField.setText("");
            if (authorWebsiteField != null) authorWebsiteField.setText("");
        } catch (Exception e) {
            // If error accessing fields, just continue
        }
    }
}