package com.backend.ems.Service.Service_Interface;

import java.util.List;

import com.backend.ems.DTO.TodoDto;
import com.backend.ems.Entity.Todo;
import com.backend.ems.Enums.TodoStatus;

public interface TodoServiceInterface {
    public boolean addTask(Todo todo);

    public List<Todo> getAllTasksByUserId(int id);

    public boolean updateTaskStatus(int id, TodoStatus status);

    public boolean deleteTask(int id);

    public boolean updateTaskDescription(int id, String newDescription);

    public List<TodoDto> getNotDoneTodoTasks(int userId);
}
