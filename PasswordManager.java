import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PasswordManager extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:password_manager.db";
    private JTable table;
    private DefaultTableModel tableModel;

    public PasswordManager() {
        setTitle("Password Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tableModel = new DefaultTableModel(new Object[]{"Content", "Email", "Password", "About"}, 0);
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRow();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRow();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRow();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadDatabase();
    }

    private void addRow() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO passwords (content, email, password, about) VALUES (?, ?, ?, ?)")) {
            
            String content = JOptionPane.showInputDialog("Enter Content");
            String email = JOptionPane.showInputDialog("Enter Email");
            String password = JOptionPane.showInputDialog("Enter Password");
            String about = JOptionPane.showInputDialog("Enter About");

            pstmt.setString(1, content);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, about);
            pstmt.executeUpdate();

            tableModel.addRow(new Object[]{content, email, password, about});
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No row selected");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE passwords SET content = ?, email = ?, password = ?, about = ? WHERE email = ?")) {
            
            String content = JOptionPane.showInputDialog("Enter Content", table.getValueAt(selectedRow, 0));
            String email = JOptionPane.showInputDialog("Enter Email", table.getValueAt(selectedRow, 1));
            String password = JOptionPane.showInputDialog("Enter Password", table.getValueAt(selectedRow, 2));
            String about = JOptionPane.showInputDialog("Enter About", table.getValueAt(selectedRow, 3));

            pstmt.setString(1, content);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, about);
            pstmt.setString(5, table.getValueAt(selectedRow, 1).toString());
            pstmt.executeUpdate();

            tableModel.setValueAt(content, selectedRow, 0);
            tableModel.setValueAt(email, selectedRow, 1);
            tableModel.setValueAt(password, selectedRow, 2);
            tableModel.setValueAt(about, selectedRow, 3);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No row selected");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM passwords WHERE email = ?")) {
            
            String email = table.getValueAt(selectedRow, 1).toString();
            pstmt.setString(1, email);
            pstmt.executeUpdate();

            tableModel.removeRow(selectedRow);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM passwords")) {
            
            while (rs.next()) {
                String content = rs.getString("content");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String about = rs.getString("about");
                tableModel.addRow(new Object[]{content, email, password, about});
                //table.getColumnModel().getColumn(1).setCellRenderer(new PasswordCellRenderer());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PasswordManager app = new PasswordManager();
            app.setVisible(true);
        });
    }
}
