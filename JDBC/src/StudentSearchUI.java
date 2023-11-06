import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentSearchUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Student");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        DefaultTableModel tableModel = new DefaultTableModel();
        JTable studentTable = new JTable(tableModel);
        tableModel.addColumn("IDStudent");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        JTextField keywordTextField = new JTextField(20);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = keywordTextField.getText();
                if (!keyword.isEmpty()) {
                    try (Connection conn = DatabaseConnector.getInstance().getConnection();
                         PreparedStatement stmt = conn.prepareStatement("SELECT IDStudent, FirstName, LastName FROM liststudent WHERE FirstName LIKE ? OR LastName LIKE ? OR IDStudent LIKE ?")) {

                        stmt.setString(1, "%" + keyword + "%");
                        stmt.setString(2, "%" + keyword + "%");
                        stmt.setString(3, "%" + keyword + "%");

                        try (ResultSet resultSet = stmt.executeQuery()) {
                            tableModel.setRowCount(0);
                            while (resultSet.next()) {
                                long id = resultSet.getLong("IDStudent");
                                String lastName = resultSet.getString("LastName");
                                String firstName = resultSet.getString("FirstName");
                                tableModel.addRow(new Object[]{id, firstName, lastName});
                            }
                        }
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }
                }
            }
        });

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Input : "));
        searchPanel.add(keywordTextField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Create UI elements for Insert, Update, and Delete
        JButton insertButton = new JButton("Insert");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JTextField idTextField = new JTextField(20);
        JTextField firstNameTextField = new JTextField(10);
        JTextField lastNameTextField = new JTextField(10);

        // Add action listeners for Insert, Update, and Delete
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = firstNameTextField.getText();
                String lastName = lastNameTextField.getText();
                insertStudent(firstName, lastName);
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long studentId = Long.parseLong(idTextField.getText());
                String firstName = firstNameTextField.getText();
                String lastName = lastNameTextField.getText();
                updateStudent(studentId, firstName, lastName);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long studentId = Long.parseLong(idTextField.getText());
                deleteStudent(studentId);
            }
        });

        JPanel crudPanel = new JPanel();
        crudPanel.add(new JLabel("ID: "));
        crudPanel.add(idTextField);
        crudPanel.add(new JLabel("First Name: "));
        crudPanel.add(firstNameTextField);
        crudPanel.add(new JLabel("Last Name: "));
        crudPanel.add(lastNameTextField);
        crudPanel.add(insertButton);
        crudPanel.add(updateButton);
        crudPanel.add(deleteButton);
        panel.add(crudPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void insertStudent(String firstName, String lastName) {
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO liststudent (FirstName, LastName) VALUES (?, ?)")) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Student added successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add student.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private static void updateStudent(long studentId, String firstName, String lastName) {
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE liststudent SET FirstName=?, LastName=? WHERE IDStudent=?")) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setLong(3, studentId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Student updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update student.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private static void deleteStudent(long studentId) {
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM liststudent WHERE IDStudent=?")) {

            stmt.setLong(1, studentId);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Student deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete student.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
