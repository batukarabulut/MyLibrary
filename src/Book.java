// Book.java
import java.sql.Date;

public class Book {
    private int bookId;
    private int authorId;
    private String title;
    private String authorName; // For display purposes
    private int year;
    private int numberOfPages;
    private String cover;
    private String about;
    private int readStatus; // 0: Not read, 1: Read, 2: Reading, 3: Want to read
    private int rating; // 0-5 stars
    private String comments;
    private Date releaseDate;

    // Constructor
    public Book(int bookId, int authorId, String title, String authorName, int year,
                int numberOfPages, String cover, String about, int readStatus,
                int rating, String comments, Date releaseDate) {
        this.bookId = bookId;
        this.authorId = authorId;
        this.title = title;
        this.authorName = authorName;
        this.year = year;
        this.numberOfPages = numberOfPages;
        this.cover = cover;
        this.about = about;
        this.readStatus = readStatus;
        this.rating = rating;
        this.comments = comments;
        this.releaseDate = releaseDate;
    }

    // Getters and Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getNumberOfPages() { return numberOfPages; }
    public void setNumberOfPages(int numberOfPages) { this.numberOfPages = numberOfPages; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public int getReadStatus() { return readStatus; }
    public void setReadStatus(int readStatus) { this.readStatus = readStatus; }

    public String getReadStatusText() {
        switch (readStatus) {
            case 0: return "Not Read";
            case 1: return "Read";
            case 2: return "Reading";
            case 3: return "Want to Read";
            default: return "Unknown";
        }
    }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    @Override
    public String toString() {
        return title + " by " + authorName + " (" + year + ")";
    }

    // For display in lists
    public String getDisplayInfo() {
        return String.format("%s by %s (%d) - %s - %s",
                title, authorName, year, getReadStatusText(), getRatingStars());
    }
}