package com.todo.gui;

import com.todo.dao.TodoAppDAO;
import com.todo.model.Todo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TodoAppGUI extends JFrame {
    private final TodoAppDAO todoDAO;
    private final JTable todoTable;
    private final DefaultTableModel tableModel;
    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JCheckBox completedCheckBox;

    public TodoAppGUI() {
        this.todoDAO = new TodoAppDAO();

        setTitle("Todo Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Description", "Completed", "Created At", "Updated At"}, 0) {
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

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        JComboBox<String> filterComboBox = new JComboBox<>(new String[]{"All", "Completed", "Pending"});

        setupLayout(addButton, updateButton, deleteButton, refreshButton, filterComboBox);
        setupEventListeners(addButton, updateButton, deleteButton, refreshButton);

        loadTodos();
    }

    private void setupLayout(JButton addButton, JButton updateButton, JButton deleteButton, JButton refreshButton, JComboBox<String> filterComboBox) {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        inputPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Description:"), gbc);
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
        statusPanel.add(new JLabel("Select a todo to update or delete."));
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners(JButton addButton, JButton updateButton, JButton deleteButton, JButton refreshButton) {
        addButton.addActionListener(e -> addTodo());
        updateButton.addActionListener(e -> updateTodo());
        deleteButton.addActionListener(e -> deleteTodo());
        refreshButton.addActionListener(e -> loadTodos());

        todoTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && todoTable.getSelectedRow() != -1) {
                int selectedRow = todoTable.getSelectedRow();
                if (selectedRow >= 0) {
                    titleField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    descriptionArea.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    completedCheckBox.setSelected((boolean) tableModel.getValueAt(selectedRow, 3));
                }
            }
        });
    }

    private void loadTodos() {
        try {
            tableModel.setRowCount(0);
            List<Todo> todos = todoDAO.getAllTodos();
            for (Todo todo : todos) {
                tableModel.addRow(new Object[]{todo.getId(), todo.getTitle(), todo.getDescription(), todo.isCompleted(), todo.getCreatedAt(), todo.getUpdatedAt()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading Todos: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTodo() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        boolean completed = completedCheckBox.isSelected();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Todo newTodo = new Todo(0, title, description, completed, null, null);

        try {
            todoDAO.addTodo(newTodo);
            loadTodos(); // Refresh the table
            clearInputFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding todo: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInputFields() {
        titleField.setText("");
        descriptionArea.setText("");
        completedCheckBox.setSelected(false);
    }

    private void updateTodo() {
        int selectedRow = todoTable.getSelectedRow();

        if (selectedRow >= 0) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String title = titleField.getText();
                String description = descriptionArea.getText();
                boolean completed = completedCheckBox.isSelected();

                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Todo updatedTodo = new Todo(id, title, description, completed, null, null);
                todoDAO.updateTodo(updatedTodo);
                loadTodos(); // Refresh the table
                clearInputFields();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating todo: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a todo to update.", "No Todo Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteTodo() {
        // Implementation for deleting a todo
    }
}
