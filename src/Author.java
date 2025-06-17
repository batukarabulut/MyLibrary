// Author.java
public class Author {
    private int authorId;
    private String name;
    private String surname;
    private String website;

    // Constructor
    public Author(int authorId, String name, String surname, String website) {
        this.authorId = authorId;
        this.name = name;
        this.surname = surname;
        this.website = website;
    }

    // Getters and Setters
    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getFullName() { return name + " " + surname; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    @Override
    public String toString() {
        return getFullName();
    }

    // For display in lists with more info
    public String getDisplayInfo() {
        return String.format("%s (Website: %s)", getFullName(),
                website != null ? website : "No website");
    }
}