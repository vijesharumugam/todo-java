package com.todo.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import com.todo.dao.TodoAppDAO;
import com.todo.model.Todo;

public class TodoAppGUI extends JFrame {
    private TodoAppDAO todoDAO;
    private JTable todoTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JCheckBox completedCheckBox;
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton refreshButton;
    private JComboBox<String> filterComboBox;

    public TodoAppGUI() {
        this.todoDAO = new TodoAppDAO();
        initializeComponents();
        setUpLayout();
        setupEventListeners();   
        loadTodos();
    }

    private void initializeComponents() {
        setTitle("Todo Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        String[] columnNames = {"ID", "Title", "Description", "Completed", "Created At", "Updated At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        todoTable = new JTable(tableModel);
        todoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        titleField = new JTextField(20);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        completedCheckBox = new JCheckBox("Completed");

        addButton = new JButton("Add Todo");
        updateButton = new JButton("Update Todo");
        deleteButton = new JButton("Delete Todo");
        refreshButton = new JButton("Refresh Todo");

        String[] filterOptions = {"All", "Completed", "Pending"};
        filterComboBox = new JComboBox<>(filterOptions);
    }

    private void setUpLayout() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Title"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Description"), gbc);
        gbc.gridx = 1;
        inputPanel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(completedCheckBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterComboBox);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(inputPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);
        northPanel.add(filterPanel, BorderLayout.NORTH);

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(todoTable), BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Select a todo to edit or delete"));
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        addButton.addActionListener(e -> addTodo());
        updateButton.addActionListener(e -> updateTodo());
        deleteButton.addActionListener(e -> deleteTodo());
        refreshButton.addActionListener(e -> refreshTodos());
        filterComboBox.addActionListener(e -> filterTodos()); 

        // Optional: load selected todo when row is clicked
        todoTable.getSelectionModel().addListSelectionListener(e -> loadSelectedTodo());
    }

    private void addTodo() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completedCheckBox.isSelected();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Title cannot be empty",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Todo newTodo = new Todo(title, description);
            newTodo.setCompleted(completed);
            todoDAO.addTodo(newTodo);

            JOptionPane.showMessageDialog(this,
                    "Todo added successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTodos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error adding todo: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTodo() {
        int row = todoTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a row to update",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String title = titleField.getText().trim();


        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Title cannot be empty",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
        Todo todo = todoDAO.getTodoById(id);
        if(todo!=null){
            todo.setTitle(title);
            todo.setDescription(descriptionArea.getText().trim());
            todo.setCompleted(completedCheckBox.isSelected());
            if(todoDAO.updateTodo(todo)){
                JOptionPane.showMessageDialog(this,
                        "Todo updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadTodos();
            }
            else{
                JOptionPane.showMessageDialog(this,
                        "Failed to update todo",
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this,
                    "Error updating todo: ",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }        
    }

    private void deleteTodo() {
        int row = todoTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                   "Please select a row to delete",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) todoTable.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this todo?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (todoDAO.deleteTodo(id)) {
                    JOptionPane.showMessageDialog(this,
                            "Todo deleted successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadTodos();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete todo",
                            "Delete Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting todo: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void refreshTodos() {
        loadTodos();
        
    }
    

    private void loadTodos() {
        try {
            List<Todo> todos = todoDAO.getAllTodos();
            updateTable(todos);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading todos: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTodos(){
        String filter = filterComboBox.getSelectedItem().toString();
        if(filter.equals("All")){
            loadTodos();
            return;
        }
        
    }
    private void loadSelectedTodo() {
        int row = todoTable.getSelectedRow();
        if (row != -1) {
            String title = (String) tableModel.getValueAt(row, 1);
            String description = (String) tableModel.getValueAt(row, 2);
            boolean completed = (Boolean) tableModel.getValueAt(row, 3); // Use boolean, not Boolean object
            titleField.setText(title);
            descriptionArea.setText(description);
            completedCheckBox.setSelected(completed); // directly set boolean
        }
    }

    private void updateTable(List<Todo> todos) {
        tableModel.setRowCount(0);  // clear existing rows
        for (Todo t : todos) {
            Object[] row = {
                    t.getId(),
                    t.getTitle(),
                    t.getDescription(),
                    t.isCompleted(),
                    t.getCreatedAt(),
                    t.getUpdatedAt()
            };
            tableModel.addRow(row);
        }
    }

    // Optional: main method to run GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TodoAppGUI gui = new TodoAppGUI();
            gui.setVisible(true);
        });
    }
}