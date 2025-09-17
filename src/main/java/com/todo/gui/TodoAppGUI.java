package com.todo.gui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.ObjectInputFilter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.todo.dao.TodoAppDAO;

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
                    //loadselectedtodo
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
            //filtertodos();
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

    }
    private void updateTodo(){ 

    }
    private void deleteTodo(){ 

    }
    private void refreshTodo(){ 

    }
    private void loadTodo(){
        
    }
}