package com.todo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.todo.model.Todo;
import com.todo.util.DatabaseConnection;

public class TodoAppDAO {

    public List<Todo> getAllTodos() {
        List<Todo> todos = new ArrayList<>();
        String sql = "SELECT * FROM todos";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                boolean completed = rs.getBoolean("completed");
                todos.add(new Todo(id, title, description, completed, rs.getTimestamp("created_at").toLocalDateTime(), rs.getTimestamp("updated_at").toLocalDateTime()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return todos;
    }
}