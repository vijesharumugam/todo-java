package com.todo;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.todo.gui.TodoAppGUI;
import com.todo.util.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        try {
            Connection cn = DatabaseConnection.getConnection();
            System.out.println("Connection Successful");
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(
            () -> {
                try{

                    new TodoAppGUI().setVisible(true);
                }
                catch(Exception e){
                    System.out.println("Error: " + e.getMessage());
                }   

            }
        );
    }
}
