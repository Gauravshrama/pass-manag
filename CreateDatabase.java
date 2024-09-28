import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDatabase {
    private static final String DB_URL = "jdbc:sqlite:password_manager.db";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Create the passwords table if it doesn't exist
            String sql = "CREATE TABLE IF NOT EXISTS passwords (" +
                    "content TEXT NOT NULL, " +
                    "email TEXT NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "about TEXT NOT NULL)";
            stmt.execute(sql);

            System.out.println("Database and table created successfully.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
