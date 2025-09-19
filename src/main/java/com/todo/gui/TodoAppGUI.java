package com.todo.gui;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.List;
import java.io.ObjectInputFilter;
import java.sql.SQLException;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

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
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JComboBox<String> filterComboBox;

    public TodoAppGUI() {
        this.todoDAO = new TodoAppDAO();
        initializeComponents();
        setupLayout();
        setupEventListers();
        loadTodos();
    }
    private void initializeComponents() {
        setTitle("Todo Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        String[] columnName = {"ID", "Title", "Description", "Completed"," Created At","Updated At"};
        tableModel = new DefaultTableModel(columnName, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        todoTable = new JTable(tableModel);
        todoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        todoTable.getSelectionModel().addListSelectionListener(
            (e) -> {
                if (!e.getValueIsAdjusting()){
                    loadSelectedTodo();
                }
            }
        );
        titleField = new JTextField(20);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        completedCheckBox = new JCheckBox("Completed");

        addButton = new JButton("Add todo");
        updateButton = new JButton("Update todo");
        deleteButton = new JButton("Delete todo");
        refreshButton = new JButton("Refresh todo");
        String[] filterOptions = {"All", "Completed", "Pending"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener((e) -> {
            filtertodos();
        });
    }
    private void setupLayout(){
        setLayout(new BorderLayout());

        JPanel inputJPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        inputJPanel.add(new JLabel("Title: "), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputJPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputJPanel.add(new JLabel("Description"),gbc);
        gbc.gridx = 1;
        inputJPanel.add(new JScrollPane(descriptionArea),gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        inputJPanel.add(completedCheckBox,gbc);
        
        JPanel ButtoPanel = new JPanel(new FlowLayout());
        ButtoPanel.add(addButton);
        ButtoPanel.add(updateButton);   
        ButtoPanel.add(deleteButton);
        ButtoPanel.add(refreshButton);
        
        add(inputJPanel,BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterComboBox.add(new JLabel("Filter:"));
        filterPanel.add(filterComboBox);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(inputJPanel,BorderLayout.CENTER);
        northPanel.add(ButtoPanel,BorderLayout.SOUTH);
        northPanel.add(filterPanel,BorderLayout.NORTH);

        add(northPanel,BorderLayout.NORTH);
        add(new JScrollPane(todoTable),BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("seletct the todo to update or delete:"));
        add(statusPanel,BorderLayout.SOUTH);
    }
    private void setupEventListers(){
        addButton.addActionListener((e) -> {addTodo();});
        updateButton.addActionListener((e) -> {
            updateTodo();
        });
        deleteButton.addActionListener((e) -> {
            deleteTodo();
        });
        refreshButton.addActionListener((e) -> {
            refreshTodo();
        });
    }
    private void addTodo(){ 
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completedCheckBox.isSelected();
        if (title.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter the title for the todo","Input error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try{
            
            Todo todo = new Todo(title,description);
            todo.setCompleted(completed);
            todoDAO.createtodo(todo);
            JOptionPane.showMessageDialog(this, "Todo added successfully","Success",JOptionPane.INFORMATION_MESSAGE);
            loadTodos(); 
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(this, "Error adding todo: "+e.getMessage(),"Database error", JOptionPane.ERROR_MESSAGE);
        }

    }
    private void filtertodos(){
        String selectedFilter = (String) filterComboBox.getSelectedItem();
        boolean selected;
        if(selectedFilter.equals("Completed")){
            selected = true;
        }
        else if(selectedFilter.equals("Pending")){
            selected = false;
        }
        else{
            loadTodos();
            return;
        }
        try{
            java.util.List<Todo> filteredTodos = todoDAO.filterTodos(selected);
            updateTable(filteredTodos);
        }catch(SQLException e)
        {
            JOptionPane.showMessageDialog(this, "Error filtering Todos"+e.getMessage(),"Database error", getDefaultCloseOperation());
        }
    }
    private void updateTodo(){ 
        int row = todoTable.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(this, "please select a todo to update ","Selection error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();

         if (title.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter the title for the todo","Validation error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id =(int) todoTable.getValueAt(row, 0);
        try{
        Todo todo = todoDAO.getTodoById(id);
        if(todo != null){
            todo.setTitle(title);
            todo.setDescription(description);
            todo.setCompleted(completedCheckBox.isSelected());
            if (todoDAO.updateTodo(todo)) {
                JOptionPane.showMessageDialog(this, "Todo updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTodos();
            }
            else{
                JOptionPane.showMessageDialog(this, "Failed to update todo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(this, "failed to update todo: "+e.getMessage(),"Database error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteTodo(){ 
        int row = todoTable.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(this, "please select a todo to delete ","Validation error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id =(int) todoTable.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this todo?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION){
            return;
        }
        try{
            if (todoDAO.deleteTodo(id)){
                JOptionPane.showMessageDialog(this, "Todo deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTodos();
            }
            else{
                JOptionPane.showMessageDialog(this, "Failed to delete todo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(this, "failed to delete todo: "+e.getMessage(),"Database error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void refreshTodo(){ 
        clearFields();
        loadTodos();
    }
    private void clearFields() {
        titleField.setText("");
        descriptionArea.setText("");
        completedCheckBox.setSelected(false);
        todoTable.clearSelection();
    }
    private void loadTodos(){
        try{

            java.util.List<Todo> todos = todoDAO.getAllTodos();
            updateTable(todos);
        }catch(SQLException e)
        {
            JOptionPane.showMessageDialog(this, "Error loading Todos"+e.getMessage(),"Database error", getDefaultCloseOperation());
        }


    }
    private void updateTable(java.util.List<Todo> todos)
    {
        tableModel.setRowCount(0);
        for(Todo t : todos )
        {
            Object[] row = {t.getId(),t.getTitle(),t.getDescription(),t.isCompleted(),t.getCreatedAt(),t.getUpdatedAt()};
            tableModel.addRow(row);
        }
    }
    private void loadSelectedTodo(){
        int row = todoTable.getSelectedRow();
        if(row != -1){
            String title = (String)tableModel.getValueAt(row, 1);
            String description = (String) tableModel.getValueAt(row, 2);
            boolean completed = (boolean)tableModel.getValueAt(row, 3);

            titleField.setText(title);
            descriptionArea.setText(description);
            completedCheckBox.setSelected(completed);    

        }
    }

}