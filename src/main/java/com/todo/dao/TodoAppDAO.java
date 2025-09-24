package com.todo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.todo.model.Todo;
import com.todo.util.DatabaseConnection;

public class TodoAppDAO {
    private static final String SELECT_ALL_STRING = "SELECT * FROM todos ORDER BY created_at DESC";
    private static final String INSERT_TODO_STRING = "INSERT INTO todos (title, description, completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_TODO_BY_ID = "SELECT * FROM todos WHERE id = ?";
    private static final String UPDATE_TODO = "UPDATE todos SET title = ?, description = ?, completed = ?, updated_at = ? WHERE id = ?";
    private static final String DELETE_TODO = "DELETE FROM todos WHERE id = ?";
    private static final String FILTER_TODO = "SELECT * FROM todos WHERE completed = ? ORDER BY created_at DESC";

    public boolean deleteTodo(int id) throws SQLException
    {
        try(
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stat = conn.prepareStatement(DELETE_TODO);
        ){
            stat.setInt(1, id);
            int affectedRow = stat.executeUpdate();
            return affectedRow > 0;
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error deleting todo: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public int createtodo(Todo todo) throws SQLException
    {
        try(
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_TODO_STRING, Statement.RETURN_GENERATED_KEYS);
            )
        {
            
            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());
            stmt.setTimestamp(4, Timestamp.valueOf(todo.getCreatedAt()));
            stmt.setTimestamp(5, Timestamp.valueOf(todo.getUpdatedAt()));
            stmt.executeUpdate();
            int rowAffected = stmt.getUpdateCount();
            if(rowAffected == 0){
                new SQLException("Creating todo failed, no row is inserted");
            }
            try(ResultSet generatedKeys = stmt.getGeneratedKeys()){
                if(generatedKeys.next()){
                    return generatedKeys.getInt(1);
                }
                else{
                    throw new SQLException("Creating todo failed, no ID obtained");
                }
            }
        }
    }

    public Todo getTodoById(int id) throws SQLException{
       
        try(
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_TODO_BY_ID);
        )
        {
            stmt.setInt(1, id);
            try(ResultSet res = stmt.executeQuery())
            {
                if(res.next()){
                    return getTodoRow(res);
                }
            }
        }
        return null;
    }

    private Todo getTodoRow(ResultSet rs) throws SQLException{
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        boolean completed = rs.getBoolean("completed");
        LocalDateTime created_at = rs.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updated_at = rs.getTimestamp("updated_at").toLocalDateTime();
        Todo todo = new Todo(id,title,description,completed,created_at,updated_at);
        return todo;
    }
    public List<Todo> filterTodos(boolean selected)throws SQLException{
        List<Todo> todos = new ArrayList<>();
        try(
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(FILTER_TODO);
        )
        {
            stmt.setBoolean(1,selected);

            ResultSet res = stmt.executeQuery();
            while(res.next()){
                todos.add(getTodoRow(res));
                
            }
        }
        return todos;

    }
    public boolean updateTodo(Todo todo) throws SQLException
    {
        try(
            Connection  conn = DatabaseConnection.getConnection();
            PreparedStatement stat = conn.prepareStatement(UPDATE_TODO);
        ){
            stat.setString(1, todo.getTitle());
            stat.setString(2, todo.getDescription());
            stat.setBoolean(3, todo.isCompleted());
            stat.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stat.setInt(5, todo.getId());
            int affectedRow = stat.executeUpdate();
            return affectedRow > 0;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    public List<Todo> getAllTodos() throws SQLException {
        List<Todo> todos = new ArrayList<>();

        

        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stat = conn.prepareStatement(SELECT_ALL_STRING);
        ResultSet res = stat.executeQuery()) {

            while (res.next()) {
                todos.add(getTodoRow(res));
            }
        }
        return todos;
    }

}