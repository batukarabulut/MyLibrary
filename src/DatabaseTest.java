import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mylibrary";
        String username = "root";
        String password = "58014833batU"; // Replace with your actual password

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Database connection successful!");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM userinfo");

            System.out.println("Users in database:");
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("userId") +
                        ", Username: " + resultSet.getString("username") +
                        ", Type: " + resultSet.getInt("userType"));
            }

            connection.close();
        } catch (Exception e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
        }
    }
}